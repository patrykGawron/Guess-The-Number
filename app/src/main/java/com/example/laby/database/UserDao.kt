package com.example.laby.database

import androidx.room.*
import com.example.laby.database.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM users WHERE username LIKE :username LIMIT 1")
    fun findByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email LIKE :email LIMIT 1")
    fun findByEmail(email: String) : User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)
}