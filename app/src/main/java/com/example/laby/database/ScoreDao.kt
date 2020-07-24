package com.example.laby.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(score: Score)

    @Delete
    fun delete(score: Score)

    @Query("SELECT * FROM scores")
    fun getAll(): List<Score>

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 10")
    fun getTop10(): LiveData<List<Score>>
}