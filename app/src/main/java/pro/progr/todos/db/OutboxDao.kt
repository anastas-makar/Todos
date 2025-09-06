package pro.progr.todos.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query

@Dao
interface OutboxDao {

    @Query("SELECT id, table_name, row_id, MAX(created_at) as created_at FROM outbox GROUP BY row_id")
    fun getSync() : List<Outbox>

    @Delete
    fun clearSync(outBoxes : List<Outbox>)
}