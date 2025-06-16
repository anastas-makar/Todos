package pro.progr.todos.dagger2

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import pro.progr.todos.CardRepository
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class,
    ViewModelModule::class,
    ViewModelFactoryModule::class])
interface DoFlowComponent {
    fun inject(application: Application)
    fun inject(cardRepository: CardRepository)
    fun cardViewModelFactory(): CardViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): DoFlowComponent
    }

}