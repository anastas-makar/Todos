package pro.progr.notecastle.model

import pro.progr.doflow.datefilters.FilterType
import pro.progr.doflow.db.NoteTag
import pro.progr.doflow.db.NotesQuery
import pro.progr.doflow.db.SublistChain


class NotesQueryBuilder() {
    constructor(oldQuery: NotesQuery) : this() {
        title = oldQuery.title
        description = oldQuery.description
        sublistChain = oldQuery.sublistChain
        tags = oldQuery.tags
        order = oldQuery.order
        noteIdToExcludeWithLinks = oldQuery.noteIdToExcludeWithLinks
        filterType = oldQuery.filterType
        notDone = oldQuery.notDone
    }

    private var title : String = ""

    fun withTitle(title : String) : NotesQueryBuilder {
        this.title = title

        return this
    }

    private var description : String = ""

    fun withDescription(description : String) : NotesQueryBuilder {
        this.description = description

        return this
    }

    private var sublistChain : SublistChain = SublistChain()

    fun withSublistChain(sublistChain : SublistChain) : NotesQueryBuilder {
        this.sublistChain = sublistChain

        return this
    }

    /*private*/ var tags : HashSet<NoteTag> = HashSet()

    fun withTags(tags : HashSet<NoteTag>) : NotesQueryBuilder {
        this.tags = tags

        return this
    }

    fun addTag(noteTag: NoteTag) : NotesQueryBuilder {
        this.tags.add(noteTag)

        return this
    }

    fun removeTag(noteTag: NoteTag) : NotesQueryBuilder {
        this.tags.remove(noteTag)

        return this
    }

    private var order : String = ""

    fun withOrder(order : String) : NotesQueryBuilder {
        this.order = order

        return this
    }

    private var noteIdToExcludeWithLinks : Long? = null

    fun withNoteIdToExcludeWithLinks(noteId : Long?) : NotesQueryBuilder {
        this.noteIdToExcludeWithLinks = noteId

        return this
    }

    private var filterType : FilterType? = null

    fun withFilterType(filterType: FilterType?) : NotesQueryBuilder {
        this.filterType = filterType

        return this
    }

    private var notDone : Boolean = false

    fun withNotDone(notDone : Boolean) : NotesQueryBuilder {
        this.notDone = notDone

        return this
    }

    fun getQuery() : NotesQuery {
        return NotesQuery(
            title = title,
            description = description,
            tags = tags,
            sublistChain = sublistChain,
            order = order,
            noteIdToExcludeWithLinks = noteIdToExcludeWithLinks,
            filterType = filterType,
            notDone = notDone
        )
    }

    fun flushQuery() : NotesQueryBuilder {
        title = ""
        description = ""
        sublistChain = SublistChain()
        tags = HashSet()
        order = ""
        noteIdToExcludeWithLinks = null
        filterType = null
        notDone = false

        return this
    }

    fun isEmpty() : Boolean {
        return title.isEmpty()
                && description.isEmpty()
                && sublistChain.isFirstLevel()
                && tags.isEmpty()
                && order.isEmpty()
                && noteIdToExcludeWithLinks == null
                && filterType == null
                && !notDone
    }
}