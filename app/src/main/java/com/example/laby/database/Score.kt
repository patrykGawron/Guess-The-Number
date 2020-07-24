package com.example.laby.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class Score(
   val score: Int = 0,
   @PrimaryKey
   val username: String = ""
)