package pro.progr.todos.dagger2

import android.content.Context
import dagger.Module
import dagger.Provides
import pro.progr.todos.CardRepository
import pro.progr.todos.DatesGridRepository
import pro.progr.todos.ListRepository
import pro.progr.todos.db.NoteAndHistoryDao
@Module
class ViewModelModule {
    @Provides
    fun provideCardViewModelFactory(repository: CardRepository): CardViewModelFactory {
        return CardViewModelFactory(repository)
    }

    @Provides
    fun provideListViewModelFactory(repository: ListRepository): ListsViewModelFactory {
        return ListsViewModelFactory(repository)
    }

    @Provides
    fun provideCalendarViewModelFactory(repository: DatesGridRepository): NoteCalendarViewModelFactory {
        return NoteCalendarViewModelFactory(repository)
    }

    @Provides
    fun provideListedCardViewModelFactory(noteAndHistoryDao: NoteAndHistoryDao, context: Context): ListedCardViewModelFactory {
        return ListedCardViewModelFactory(noteAndHistoryDao, context)
    }
}
