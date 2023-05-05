package com.example.muzecode

import androidx.room.*

@Dao
interface SongQueueDao {
    //GET THE NUMBER OF ROWS IN songQueue
    @Query("SELECT COUNT(*) FROM songQueue")
    fun songQueueRowCount(): Int

    //GET THE FIRST ROW AKA QUEUE, WHICH IS THE NAME OF A FILE !!
    @Query("SELECT * FROM songQueue LIMIT 1")
    fun getFirstRow(): Song

    //GET ALL SONGS IN THE SONGQUEUE TABLE
    @Query("SELECT * FROM songQueue")
    fun getAll(): List<String>

    @Query("UPDATE songQueue SET songURI = :newName WHERE songURI = (SELECT * FROM songQueue LIMIT 1)")
    fun updateSongRow(newName: String)
    //INSERT A ROW INTO THE SONGQUEUE TABLE
    @Insert
    fun insertSongUri(songUri: Song)
    //DELETE ALL SONGS FROM THE SONGQUEUE TABLE
    @Query("DELETE FROM songQueue")
    fun deleteAllQueue()
}

//SONG QUEUE TABLE
//FIRST ROW IS ALWAYS GOING TO BE THE INDEX IN THE QUEUE FROM WHICH THEY LEFT OFF
@Entity(tableName = "songQueue", primaryKeys = ["songURI"])
data class Song(
    @ColumnInfo(name = "songURI") var songUri: String,
)

@Database(entities = [Song::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songQueueDao(): SongQueueDao
}