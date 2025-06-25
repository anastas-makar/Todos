package pro.progr.todos.dagger2

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class,
    ViewModelModule::class,
    ViewModelFactoryModule::class,
    DatabaseModule::class,
    PaletteModule::class,
    AppModule::class])
interface TodosComponent {
    fun cardViewModelFactory(): CardViewModelFactory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun build(): TodosComponent
    }

}