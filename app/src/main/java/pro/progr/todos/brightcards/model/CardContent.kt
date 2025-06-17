package pro.progr.todos.brightcards.model

import pro.progr.todos.brightcards.MenuItem
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.colors.GouachePalette

data class CardContent(
    var id: String? = null,
    var title: String = "",
    var text: String = "",
    var style: ColorStyle = GouachePalette.values().random(),
    var fillTitleBackground: Boolean = true,
    var fillTextBackground: Boolean = true,
    var imageUrls: List<String> = emptyList(),
    var tags: List<CardTag> = emptyList(),
    var todo: TodoStatus = TodoStatus.NOT_ACTIVE,
    var stringAnnotations: List<StringAnnotation> = emptyList(),
    val reward : Int = 10,
    var latestDone: Long? = null,
    var menuItems: List<MenuItem> = emptyList()
)