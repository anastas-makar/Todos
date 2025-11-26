package pro.progr.todos.api.model

data class NoteDto(
    val id: String,
    val date: Long,
    val title: String,
    val description: String,
    val sublistChain: SublistChainDto,
    val reward: Int,
    val addedDates: List<Long>,
    val cancelledDates: List<Long>,
    val patterDates: Map<Long, ScheduleDayDto>,
    val patternType: String,
    val dateSince: Long?,
    val dateTill: Long?,
    val style: String,
    val fillTitleBackground: Boolean,
    val fillTextBackground: Boolean,
    val todo: String,
    val latestDone: Long?,
    val deleted: Boolean,
    val schedule: String? = null,
    val latestUpdate: Long?
)