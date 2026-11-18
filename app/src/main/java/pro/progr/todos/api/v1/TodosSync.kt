package pro.progr.todos.api.v1

import com.google.gson.annotations.SerializedName
import pro.progr.todos.api.v1.model.DiamondsLogDto
import pro.progr.todos.api.v1.model.NoteDto
import pro.progr.todos.api.v1.model.NoteDateEntryDto
import pro.progr.todos.api.v1.model.NoteTagDto
import pro.progr.todos.api.v1.model.NoteToTagXRefDto
import pro.progr.todos.api.v1.model.NotesListDto

data class TodosSync(
    val syncMetaData: SyncMetaData,
    val notes : List<NoteDto>,
    @SerializedName("notesInHistory") val noteDateEntries : List<NoteDateEntryDto>,
    val notesLists: List<NotesListDto>,
    val noteTags: List<NoteTagDto>,
    val noteToTags: List<NoteToTagXRefDto>,
    val diamondLogs: List<DiamondsLogDto>
)
