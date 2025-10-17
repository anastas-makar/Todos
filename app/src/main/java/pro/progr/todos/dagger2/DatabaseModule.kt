package pro.progr.todos.dagger2

import android.content.Context
import dagger.Module
import dagger.Provides
import pro.progr.todos.db.*
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): TodosDataBase {
        return TodosDataBase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideNotesDao(database: TodosDataBase): NotesDao {
        return database.notesDao()
    }

    @Singleton
    @Provides
    fun provideNoteListsDao(database: TodosDataBase): NoteListsDao {
        return database.noteListsDao()
    }

    @Singleton
    @Provides
    fun provideNoteToTagXRefDao(database: TodosDataBase): NoteToTagXRefDao {
        return database.noteToTagXRefDao()
    }

    @Singleton
    @Provides
    fun provideOutboxDao(database: TodosDataBase): OutboxDao {
        return database.outBoxDao()
    }

    @Singleton
    @Provides
    fun provideDiamondsLogDao(database: TodosDataBase): DiamondsLogDao {
        return database.diamondsLogDao()
    }

    @Singleton
    @Provides
    fun provideDiamondsLogServerReceivedDao(database: TodosDataBase): DiamondsLogServerReceivedDao {
        return database.diamondsLogServerReceivedDao()
    }

    @Singleton
    @Provides
    fun provideNoteAndHistoryDao(database: TodosDataBase): NoteAndHistoryDao {
        return database.noteAndHistoryDao()
    }

    @Singleton
    @Provides
    fun provideNoteInHistoryDao(database: TodosDataBase): NotesInHistoryDao {
        return database.notesInHistoryDao()
    }

    @Singleton
    @Provides
    fun provideNotesWithDataDao(database: TodosDataBase): NoteWithDataDao {
        return database.noteWithDataDao()
    }

    @Singleton
    @Provides
    fun provideDiamondsCountDao(database: TodosDataBase): DiamondsCountDao {
        return database.diamondsCountDao()
    }

    @Singleton
    @Provides
    fun provideTagsDao(database: TodosDataBase): TagsDao {
        return database.tagsDao()
    }
}