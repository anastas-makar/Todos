package pro.progr.todos

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.TagsDao
import pro.progr.todos.db.TodosDataBase
import javax.inject.Inject

class TagsRepository @Inject constructor(private val db: TodosDataBase) {
    private val tagsDao: TagsDao = db.tagsDao()
    private val noteToTagDao = db.noteToTagXRefDao()

    val tagsFlow: Flow<List<NoteTag>> = tagsDao.getAllTags()

    /** Tags are case-sensitive; only whitespace at the two ends is ignored. */
    suspend fun findOrCreate(title: String): NoteTag? {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isEmpty()) return null

        return db.withTransaction {
            reconcileDuplicatesInTransaction()
            val existing = tagsDao.getActiveTags()
                .firstOrNull { it.title.trim() == normalizedTitle }

            if (existing != null) {
                val updated = existing.copy(title = normalizedTitle, hidden = false)
                if (existing.title != updated.title || existing.hidden != updated.hidden) {
                    tagsDao.update(updated)
                }
                updated
            } else {
                NoteTag(title = normalizedTitle).also { tagsDao.insert(it) }
            }
        }
    }

    suspend fun setHidden(tagId: String, hidden: Boolean, protectedTagIds: Set<String> = emptySet()) {
        if (hidden && tagId in protectedTagIds) return
        db.withTransaction {
            val current = tagsDao.getActiveTag(tagId) ?: return@withTransaction
            if (current.hidden != hidden) tagsDao.update(current.copy(hidden = hidden))
        }
    }

    /**
     * Reconcile same-name tags after applying a sync response. A deterministic
     * UUID choice makes every device converge on the same surviving tag.
     */
    suspend fun reconcileDuplicates() {
        db.withTransaction { reconcileDuplicatesInTransaction() }
    }

    private suspend fun reconcileDuplicatesInTransaction() {
        val groups = tagsDao.getAllTagsOnce()
            .filter { it.title.trim().isNotEmpty() }
            .groupBy { it.title.trim() }

        for ((normalizedTitle, group) in groups) {
            val activeTags = group.filterNot { it.deleted }
            if (activeTags.isEmpty()) continue

            // A previously tombstoned duplicate can still have stale active links
            // on an offline device. Prefer an active record as the survivor.
            val canonical = activeTags.minByOrNull { it.id } ?: continue
            val duplicateTags = group.filter { it.id != canonical.id }
            val mergedHidden = activeTags.all { it.hidden }
            val normalizedCanonical = canonical.copy(
                title = normalizedTitle,
                hidden = mergedHidden
            )
            if (canonical.title != normalizedCanonical.title || canonical.hidden != normalizedCanonical.hidden) {
                tagsDao.update(normalizedCanonical)
            }

            if (duplicateTags.isEmpty()) continue

            val groupIds = group.map { it.id }
            val linksByNote = noteToTagDao.getActiveByTagIds(groupIds).groupBy { it.noteId }
            for (links in linksByNote.values) {
                val canonicalLink = links.filter { it.tagId == canonical.id }.minByOrNull { it.id }
                val keep = canonicalLink ?: links.minByOrNull { it.id } ?: continue
                if (keep.tagId != canonical.id) noteToTagDao.reassignTag(keep.id, canonical.id)
                links.filter { it.id != keep.id }.forEach { duplicateLink ->
                    noteToTagDao.tombstone(duplicateLink.id)
                }
            }

            duplicateTags.filterNot { it.deleted }.forEach { duplicate ->
                tagsDao.update(duplicate.copy(deleted = true))
            }
        }
    }
}
