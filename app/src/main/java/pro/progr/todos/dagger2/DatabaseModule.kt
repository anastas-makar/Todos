package pro.progr.todos.dagger2

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import pro.progr.todos.db.*
import javax.inject.Singleton
import pro.progr.todos.db.MIGRATION_1_2

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDoFlowDatabase(context: Context): DoFlowDataBase {
        return Room.databaseBuilder(
            context,
            DoFlowDataBase::class.java, "doFlow-database"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3, MIGRATION_1_4, MIGRATION_2_4, MIGRATION_3_4)
            .build()
    }

    @Singleton
    @Provides
    fun provideNotesDao(database: DoFlowDataBase): NotesDao {
        return database.notesDao()
    }

    @Singleton
    @Provides
    fun provideNoteListsDao(database: DoFlowDataBase): NoteListsDao {
        return database.noteListsDao()
    }

    @Singleton
    @Provides
    fun provideNoteAndHistoryDao(database: DoFlowDataBase): NoteAndHistoryDao {
        return database.noteAndHistoryDao()
    }

    @Singleton
    @Provides
    fun provideNoteInHistoryDao(database: DoFlowDataBase): NotesInHistoryDao {
        return database.notesInHistoryDao()
    }

    @Singleton
    @Provides
    fun provideNotesWithDataDao(database: DoFlowDataBase): NoteWithDataDao {
        return database.noteWithDataDao()
    }

    @Singleton
    @Provides
    fun provideDiamondsCountDao(database: DoFlowDataBase): DiamondsCountDao {
        return database.diamondsCountDao()
    }

    @Singleton
    @Provides
    fun provideTagsDao(database: DoFlowDataBase): TagsDao {
        return database.tagsDao()
    }
}