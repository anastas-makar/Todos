package pro.progr.todos.db

import pro.progr.todos.datefilters.FilterType
import javax.inject.Inject

class NotesQuery(
    title : String,
    description : String,
    sublistChain : SublistChain,
    tags : HashSet<NoteTag>,
    order : String,
    noteIdToExcludeWithLinks : Long?,
    filterType : FilterType? = null,
    notDone : Boolean = false
    ) {
    @Inject
    constructor() : this("", "", SublistChain(), HashSet(), "", null)

    val title : String
    var description : String
    var sublistChain : SublistChain
    var tags : HashSet<NoteTag>
    val order : String
    val noteIdToExcludeWithLinks : Long?
    val filterType : FilterType?
    var notDone : Boolean = false

    init {
        this.title = title
        this.description = description
        this.sublistChain = sublistChain
        this.tags = tags
        this.order = order
        this.noteIdToExcludeWithLinks = noteIdToExcludeWithLinks
        this.filterType = filterType
        this.notDone = notDone
    }

    val queryString = "SELECT * FROM notes WHERE "
        get() {
            return field +
                    (if (description.isNotEmpty()) "title || description LIKE :description AND " else "") +
                         " sublist_chain LIKE :sublistChain " +
                    (if (tags.isEmpty()) "" else " AND id IN (SELECT note_id FROM note_to_tag WHERE tag_id IN (" +
                            tags.joinToString(separator = ",", transform = { "\'${it.id}\'" }) +")) " )+
                    (if (noteIdToExcludeWithLinks == null)  ""
                        else " AND id NOT IN (SELECT note_from FROM note_to_note WHERE note_to = ${noteIdToExcludeWithLinks}) " +
                            "AND id <> ${noteIdToExcludeWithLinks} " ) +
                    (if (filterType == null) "" else " AND pattern_type = '${filterType}' ") +
                    (if (notDone) " AND todo <> 'DONE' " else "") +
                    " ORDER BY id DESC"
        }

    val historyQueryString = "SELECT * FROM notes_in_history WHERE "
        get() {
            return field +
                    (if (description.isNotEmpty()) "title || description LIKE :description AND " else "") +
                    " sublist_chain LIKE :sublistChain " +
                    (if (tags.isEmpty()) "" else " AND noteId IN (SELECT note_id FROM note_to_tag WHERE tag_id IN (" +
                            tags.joinToString(separator = ",", transform = { "\'${it.id}\'" }) +")) " ) +
                    " ORDER BY id DESC"
        }

    val queryArguments = ArrayList<Any>()
        get() {
            field.clear()

            if(description.isNotEmpty()) {
                field.add("%${description}%")
            }

            field.add("${sublistChain.sublistsString}%")

            return field
        }

    fun isChanged() : Boolean {
        return !isEmpty()
    }

    fun isEmpty() : Boolean {
        return title.isEmpty()
                && description.isEmpty()
                && sublistChain.containsAll()
                && tags.isEmpty()
                && order.isEmpty()
                && noteIdToExcludeWithLinks == null
                && filterType == null
                && !notDone
    }
}