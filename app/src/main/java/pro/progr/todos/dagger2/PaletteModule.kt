package pro.progr.todos.dagger2

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import pro.progr.todos.PaletteRepository

@Module
class PaletteModule {

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PaletteRepository.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }
}
