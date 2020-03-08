package com.rydvi.clean_vrn.ui.login

import com.rydvi.clean_vrn.ui.login.LoggedInUserView

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)
