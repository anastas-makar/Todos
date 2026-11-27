package pro.progr.todos.dagger2

import dagger.Binds
import dagger.Module
import pro.progr.diamondapi.AccumulateInterface
import pro.progr.diamondapi.ExchangeInterface
import pro.progr.todos.DiamondsCountRepository

@Module
interface ExchangeBindingModule {

    @Binds
    fun bindExchangeInterface(
        repository: DiamondsCountRepository
    ): ExchangeInterface

    @Binds
    fun bindAccumulateInterface(
        repository: DiamondsCountRepository
    ): AccumulateInterface
}
