package pro.progr.todos.dagger2

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import pro.progr.authapi.AuthInterface
import pro.progr.diagnostics.Diagnostics
import pro.progr.diamondapi.AccumulateInterface
import pro.progr.diamondapi.ExchangeInterface
import pro.progr.personalcrypto.PersonalCrypto
import pro.progr.todos.SyncRepository
import pro.progr.todos.api.TodosApiService
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class,
    ViewModelModule::class,
    ViewModelFactoryModule::class,
    PaletteModule::class,
    SyncModule::class,
    AppModule::class,
    TodosNetworkModule::class,
    ExchangeBindingModule::class])
interface TodosComponent {
    fun exchangeInterface(): ExchangeInterface

    fun accumulateInterface(): AccumulateInterface

    fun syncRepository(): SyncRepository

    fun cardViewModelFactory(): CardViewModelFactory

    fun listsViewModelFactory(): ListsViewModelFactory

    fun daggerViewModelFactory(): DaggerViewModelFactory

    fun paletteViewModelFactory(): PaletteViewModelFactory

    fun api(): TodosApiService

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun auth(auth: AuthInterface): Builder

        @BindsInstance
        fun personalCrypto(personalCrypto: PersonalCrypto): Builder

        @BindsInstance
        fun diagnostics(diagnostics: Diagnostics): Builder

        fun appModule(appModule: AppModule): Builder

        fun build(): TodosComponent
    }

}
