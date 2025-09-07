package pro.progr.todos.api

import pro.progr.todos.db.DiamondsLog
import pro.progr.todos.db.Note
import pro.progr.todos.db.NoteInHistory
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.NoteToTagXRef
import pro.progr.todos.db.NotesList

data class TodosSync(
    val syncMetaData: SyncMetaData,
    val notes : List<Note>,
    val notesInHistory : List<NoteInHistory>,
    val notesLists: List<NotesList>,
    val noteTags: List<NoteTag>,
    val noteToTags: List<NoteToTagXRef>,
    val diamondLogs: List<DiamondsLog>
)