package pro.progr.todos.dagger2

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import pro.progr.todos.DiamondsCountRepository
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class,
    ViewModelModule::class,
    ViewModelFactoryModule::class,
    PaletteModule::class,
    SyncModule::class,
    AppModule::class])
interface TodosComponent {
    fun diamondsCountRepository(): DiamondsCountRepository

    fun cardViewModelFactory(): CardViewModelFactory

    fun listsViewModelFactory(): ListsViewModelFactory

    fun daggerViewModelFactory(): DaggerViewModelFactory

    fun listedCardViewModelFactory(): ListedCardViewModelFactory

    fun noteCalendarViewModelFactory(): NoteCalendarViewModelFactory

    fun paletteViewModelFactory(): PaletteViewModelFactory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun build(): TodosComponent
    }

}