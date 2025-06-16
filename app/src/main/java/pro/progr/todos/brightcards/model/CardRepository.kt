package pro.progr.brightcards.model

interface CardRepository {

    suspend fun getCard(id: String): CardContent

    suspend fun saveCard(cardContent: CardContent) : CardContent

    suspend fun updateCard(cardContent: CardContent)

    suspend fun deleteCard(cardContent: CardContent): Boolean

    suspend fun setCardDone(cardContent: CardContent)
}