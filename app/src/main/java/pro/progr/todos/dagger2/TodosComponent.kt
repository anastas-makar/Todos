package pro.progr.todos.dagger2

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import pro.progr.todos.DiamondsCountRepository
import pro.progr.todos.api.TodosApiService
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class,
    ViewModelModule::class,
    ViewModelFactoryModule::class,
    PaletteModule::class,
    SyncModule::class,
    AppModule::class,
    TodosNetworkModule::class])
interface TodosComponent {
    fun diamondsCountRepository(): DiamondsCountRepository

    fun cardViewModelFactory(): CardViewModelFactory

    fun listsViewModelFactory(): ListsViewModelFactory

    fun daggerViewModelFactory(): DaggerViewModelFactory

    fun listedCardViewModelFactory(): ListedCardViewModelFactory

    fun noteCalendarViewModelFactory(): NoteCalendarViewModelFactory

    fun paletteViewModelFactory(): PaletteViewModelFactory

    fun api(): TodosApiService

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun build(): TodosComponent
    }

}