package com.example.laby.data

import android.app.Application
import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.laby.MainApplication
import com.example.laby.data.model.LoggedInUser
import com.example.laby.database.User
import com.example.laby.database.UserDao
import com.example.laby.database.UserDatabase
import com.example.laby.ui.login.LoginActivity
import kotlinx.coroutines.*
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private var dataSourceJob = Job()

    private val uiScope = CoroutineScope(dataSourceJob)

    lateinit var database: UserDao

    private var userToLogin: User? = null

    fun register(
        username: String,
        email: String,
        password: String,
        application: Application): Result<LoggedInUser> = runBlocking {
        var registered: Result<LoggedInUser> = Result.WrongCredentials("Not found")
        database = UserDatabase.getInstance(application).userDao
        val aJob = uiScope.launch {
            registered = registerNewUser(username, email, password)
        }
        aJob.join()
        if (registered is Result.Success)
            return@runBlocking  login(username, email, password, application)
        else
            return@runBlocking  registered
    }

    fun login(
        username: String,
        email: String,
        password: String,
        application: Application
    ): Result<LoggedInUser> = runBlocking {
        try {
            var authorized: Result<LoggedInUser> = Result.WrongCredentials("Not found")
            database = UserDatabase.getInstance(application).userDao
            val aJob = uiScope.launch {
                authorized = authorize(username, email, password)
            }
            aJob.join()
            val sharedPref = application.getSharedPreferences(
                "userData",
                Context.MODE_PRIVATE
            )
            with(sharedPref.edit()) {
                putString("username", username)
                apply()
            }
            return@runBlocking authorized
        } catch (e: Throwable) {
            return@runBlocking Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    private suspend fun authorize(
        username: String,
        email: String,
        password: String
    ): Result<LoggedInUser> {
        return withContext(Dispatchers.IO) {
            userToLogin = database.findByEmail(email)
            if (userToLogin != null) {
                if (userToLogin?.username == username) {
                    if (userToLogin?.password == password) {
                        Result.Success(
                            LoggedInUser(
                                userToLogin?.uid.toString(),
                                userToLogin?.username
                            )
                        )
                    } else {
                        Result.WrongCredentials("Wrong password")
                    }
                } else {
                    Result.WrongCredentials("Wrong username")
                }
            } else {
                Result.WrongCredentials("Wrong email")
            }
        }
    }

    private suspend fun registerNewUser(
        username: String,
        email: String,
        password: String): Result<LoggedInUser> {
        return withContext(Dispatchers.IO) {
            val userByEmail = database.findByEmail(email)
            val userByUsername = database.findByUsername(username)

            if (userByEmail == null && userByUsername == null) {
                val user = User(
                    username = username,
                    email = email,
                    password = password)
                database.insert(user)
                val userFromDb = database.findByEmail(email)
                return@withContext Result.Success(LoggedInUser(
                    userFromDb?.uid.toString(),
                    userFromDb?.username
                ))
            }
            if (userByEmail != null) {
                return@withContext Result.WrongCredentials("Email already taken")
            }
            if (userByUsername != null) {
                Log.i("LoginDataSource", "This username is already taken")
                return@withContext Result.WrongCredentials("Username already taken")
            }
            return@withContext Result.WrongCredentials("Register failed")
        }
    }
}


