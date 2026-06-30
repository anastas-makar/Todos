package pro.progr.todos.dagger2

import dagger.Binds
import dagger.Module
import pro.progr.diamondapi.PurchaseInterface
import pro.progr.todos.DiamondsCountRepository

@Module
interface PurchaseBindingModule {

    @Binds
    fun bindPurchaseInterface(
        repository: DiamondsCountRepository
    ): PurchaseInterface
}