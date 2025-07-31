package pro.progr.todos.brightcards.model

import java.time.LocalDate

interface ListedCardRepositoryInterface {
    suspend fun setCardDone(cardContent: CardContent)
    suspend fun setNoteInHistoryDone(cardContent: CardContent, date: LocalDate)
    suspend fun setCardNotDone(cardContent: CardContent)

    suspend fun removeForDay(date: LocalDate, noteId: String)
    suspend fun addCardToHistory(cardContent: CardContent, date: LocalDate)
    suspend fun addCardForDay(cardContent: CardContent, date: LocalDate)
}