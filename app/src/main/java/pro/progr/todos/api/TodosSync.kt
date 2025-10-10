package pro.progr.todos.api

import pro.progr.todos.api.model.DiamondsLogDto
import pro.progr.todos.api.model.NoteDto
import pro.progr.todos.api.model.NoteInHistoryDto
import pro.progr.todos.api.model.NoteTagDto
import pro.progr.todos.api.model.NoteToTagXRefDto
import pro.progr.todos.api.model.NotesListDto

data class TodosSync(
    val syncMetaData: SyncMetaData,
    val notes : List<NoteDto>,
    val notesInHistory : List<NoteInHistoryDto>,
    val notesLists: List<NotesListDto>,
    val noteTags: List<NoteTagDto>,
    val noteToTags: List<NoteToTagXRefDto>,
    val diamondLogs: List<DiamondsLogDto>
)