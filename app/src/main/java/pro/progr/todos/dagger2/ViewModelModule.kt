package pro.progr.doflow.dagger2

import dagger.Module
import dagger.Provides
import pro.progr.doflow.CardRepository
import pro.progr.doflow.DatesGridRepository
import pro.progr.doflow.ListRepository
import pro.progr.doflow.db.NoteAndHistoryDao
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
    fun provideListedCardViewModelFactory(noteAndHistoryDao: NoteAndHistoryDao): ListedCardViewModelFactory {
        return ListedCardViewModelFactory(noteAndHistoryDao)
    }
}