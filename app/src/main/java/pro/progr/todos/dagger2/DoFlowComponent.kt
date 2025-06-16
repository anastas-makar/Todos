package pro.progr.doflow.dagger2

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import pro.progr.doflow.CardRepository
import pro.progr.doflow.DoFlow
import pro.progr.doflow.FlowActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class,
    ViewModelModule::class,
    ViewModelFactoryModule::class])
interface DoFlowComponent {
    fun inject(doFlow: DoFlow)
    fun inject(cardRepository: CardRepository)
    fun inject(flowActivity: FlowActivity)
    fun cardViewModelFactory(): CardViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): DoFlowComponent
    }

}