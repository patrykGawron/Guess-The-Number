package com.example.laby.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(tableName = "users", primaryKeys = ["username", "email"])
data class User(
    val uid: Long = Random.nextLong(),
    var username: String = "",
    var email: String = "",
    var password: String = ""
)