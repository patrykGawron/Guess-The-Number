package com.example.laby.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val message: String = "",
    val error: Int? = null
)
