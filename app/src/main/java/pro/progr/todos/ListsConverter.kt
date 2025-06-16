package pro.progr.doflow

import pro.progr.doflow.db.NotesList

class ListsConverter {
    companion object {
        fun toNotesList(nList: NList) : NotesList {

            return NotesList(
                id = nList.id,
                title = nList.lname,
                isCurrent = false,
                sublistChain = nList.sublistChain
            )
        }

        fun toNList(notesList: NotesList) : NList {
            return NList(notesList.title, notesList.sublistChain, notesList.id)
        }

    }
}