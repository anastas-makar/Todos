package pro.progr.doflow

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pro.progr.brightcards.vm.ListedCardViewModel
import pro.progr.brightcards.vm.PaletteViewModel
import pro.progr.doflow.dagger2.*
import javax.inject.Inject

class FlowActivity : AppCompatActivity() {

    @Inject
    lateinit var cardRepository: CardRepository

    @Inject
    lateinit var cardViewModelFactory: CardViewModelFactory

    @Inject
    lateinit var noteCalendarViewModelFactory: NoteCalendarViewModelFactory

    @Inject
    lateinit var listedCardViewModelFactory: ListedCardViewModelFactory

    @Inject
    lateinit var listsViewModelFactory: ListsViewModelFactory

    @Inject
    lateinit var daggerViewModelFactory: DaggerViewModelFactory

    @Inject
    lateinit var diamondsCountRepository: DiamondsCountRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    val cardsListViewModel: CardsListViewModel by viewModels { daggerViewModelFactory }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        (application as DoFlow).appComponent.inject(this)

        super.onCreate(savedInstanceState)

        val uri = intent.data
        val animalId = uri?.lastPathSegment

        setContent {

            val historyState = cardsListViewModel.getHistoryFlow().collectAsState(null)

            cardsListViewModel.setViewModelCreator { note, cardContent, date ->
                listedCardViewModelFactory
                    .createWithCardContent(cardContent)
                    .createWithNote(note)

                ViewModelProvider(this, listedCardViewModelFactory).get(note.toString() + date?.toString(), ListedCardViewModel::class.java)
            }

            AppNavigation(
                daggerViewModelFactory,
                cardViewModelFactory,
                noteCalendarViewModelFactory,
                listsViewModelFactory,
                PaletteViewModel(PaletteRepository(getSharedPreferences(PaletteRepository.SHARED_PREFS_NAME, MODE_PRIVATE))),
                cardsListViewModel,
                historyState,
                diamondsCountRepository,
                startDestination =
                    if (animalId != null)
                        "animal?id=${Uri.encode(animalId)}"
                    else
                        "calendar"
            )

            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}