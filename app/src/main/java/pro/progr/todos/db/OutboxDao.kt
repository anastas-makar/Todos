package pro.progr.todos.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query

@Dao
interface OutboxDao {

    @Query("SELECT * FROM outbox")
    fun getSync() : List<Outbox>

    @Delete
    fun clearSync(outBoxes : List<Outbox>)
}