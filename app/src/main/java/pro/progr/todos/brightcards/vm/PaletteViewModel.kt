package pro.progr.brightcards.vm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pro.progr.brightcards.carousel.Carousel
import pro.progr.brightcards.colors.ColorsPage
import pro.progr.brightcards.colors.PaletteType
import pro.progr.brightcards.model.PaletteRepository

class PaletteViewModel(val repository: PaletteRepository): ViewModel() {

    private val carousel : Carousel<ColorsPage>
    val currentPage : MutableState<ColorsPage>

    val defaultPalette: MutableState<ColorsPage> = mutableStateOf(ColorsPage(PaletteType.PASTEL))

    init {
        carousel = getCarousel()
        currentPage = mutableStateOf(carousel.current())
    }

    fun swipeToDefault() {
        carousel.swipeTo(getDefaultPalette())
        currentPage.value = carousel.current()
    }

    fun getDefaultPalette() : ColorsPage  {
        viewModelScope.launch {
            defaultPalette.value = repository.getDefaultPalette()
        }

        return defaultPalette.value
    }

    fun updateDefaultPalette(palette: ColorsPage) {
        defaultPalette.value = palette
        viewModelScope.launch {
            repository.updateDefaultPalette(palette)
        }
    }

    fun getCarousel(): Carousel<ColorsPage> {
        val carousel = Carousel(PaletteType.values().map {
            type -> ColorsPage(type)
        })
        carousel.swipeTo(getDefaultPalette())

        return carousel
    }

    fun prevPage() {
        currentPage.value = carousel.prev()
    }

    fun nextPage() {
        currentPage.value = carousel.next()
    }
}