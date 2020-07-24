package com.example.laby.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.example.laby.data.LoginRepository
import com.example.laby.data.Result

import com.example.laby.R
import com.example.laby.database.User
import com.example.laby.database.UserDao
import com.example.laby.database.UserDatabase
import kotlinx.coroutines.*

class LoginViewModel(private val loginRepository: LoginRepository,
                     val database: UserDao,
                     application: Application) : AndroidViewModel(application) {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(application: Application, username: String, email: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(application, username, email, password)

        _loginResult.value = when(result){
            is Result.Success -> LoginResult(success =
                LoggedInUserView(displayName = result.data.displayName))
            is Result.WrongCredentials -> LoginResult(message = result.data)
            is Result.Error -> LoginResult(error = R.string.login_failed)
        }
    }

    fun register(application: Application, username: String, email: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.register(application, username, email, password)

        _loginResult.value = when(result){
            is Result.Success -> LoginResult(success =
            LoggedInUserView(displayName = result.data.displayName))
            is Result.WrongCredentials -> LoginResult(message = result.data)
            is Result.Error -> LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }


}
