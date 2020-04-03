package com.rydvi.clean_vrn.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.rydvi.clean_vrn.ui.data.LoginRepository
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Error
import com.rydvi.clean_vrn.api.Session

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    val session: MutableLiveData<Session>? = null

    fun login(
        username: String,
        password: String,
        isPlayer: Boolean,
        callbackSuccess: (Session) -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

//        if (result is Result.Success) {
//            _loginResult.value =
//                LoginResult(
//                    success = LoggedInUserView(
//                        displayName = result.data.displayName
//                    )
//                )
//        } else {
//            _loginResult.value =
//                LoginResult(error = R.string.login_failed)
//        }

        DataRepository.login(username, password, isPlayer, callbackSuccess, callbackFailed)
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value =
                LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value =
                LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value =
                LoginFormState(isDataValid = true)
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
        return password.length > 1
    }
}
