package pro.progr.todos.dagger2

import dagger.Module
import dagger.Provides
import pro.progr.diagnostics.Diagnostics
import pro.progr.todos.CardRepository
import pro.progr.todos.ListRepository
@Module
class ViewModelModule {
    @Provides
    fun provideCardViewModelFactory(
        repository: CardRepository,
        diagnostics: Diagnostics
    ): CardViewModelFactory {
        return CardViewModelFactory(repository, diagnostics)
    }

    @Provides
    fun provideListViewModelFactory(repository: ListRepository): ListsViewModelFactory {
        return ListsViewModelFactory(repository)
    }

}
