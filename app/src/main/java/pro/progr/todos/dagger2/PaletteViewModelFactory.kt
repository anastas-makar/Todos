package pro.progr.todos.dagger2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pro.progr.todos.PaletteRepository
import pro.progr.todos.brightcards.vm.PaletteViewModel
import javax.inject.Inject

class PaletteViewModelFactory @Inject constructor(
    private val repository: PaletteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaletteViewModel::class.java)) {
            return PaletteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
