package pro.progr.todos.dagger2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.todos.CardsListViewModel
import pro.progr.todos.DiamondViewModel
import pro.progr.todos.TagsViewModel

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ExperimentalCoroutinesApi
    @ViewModelKey(CardsListViewModel::class)
    abstract fun cardsListViewModel(viewModel: CardsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DiamondViewModel::class)
    abstract fun drawerViewModel(viewModel: DiamondViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TagsViewModel::class)
    abstract fun tagsViewModel(viewModel: TagsViewModel): ViewModel

    // other ViewModels
}