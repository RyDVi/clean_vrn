package com.rydvi.clean_vrn.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.ui.error.ErrorHandler
import com.rydvi.clean_vrn.ui.teams.TeamCreateEditFragment
import com.rydvi.clean_vrn.ui.utils.getCreateEditModeByString

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    var errorHandler: ErrorHandler = ErrorHandler(this)
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Сокрытие тулбара
        supportActionBar?.hide()

        DataRepository.activity = this

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loginAsPlayer = findViewById<Button>(R.id.loginAsPlayer)
        loading = findViewById(R.id.loading)

        loginViewModel = ViewModelProviders.of(
            this,
            LoginViewModelFactory()
        )
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

//            setOnEditorActionListener { _, actionId, _ ->
//                when (actionId) {
//                    EditorInfo.IME_ACTION_DONE ->
//                        loginViewModel.login(
//                            username.text.toString(),
//                            password.text.toString()
//                        )
//                }
//                false
//            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString(), false, {
                    //Сохранение учетных данных
                    UserData(
                        this@LoginActivity,
                        username = username.text.toString(),
                        password = password.text.toString()
                    ).save()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                    { }
                )
            }

            loginAsPlayer.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString(), true, {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }, { }
                )
            }
        }

        if(intent.getBooleanExtra(LOGOUT_NAV_EXTRA, false)){
            UserData.clear(this)
        } else {
            tryLoadUserData(this)
        }
    }

    /**
     * Попытка загрузки учетных данных
     */
    private fun tryLoadUserData(context: Context) {
        val userData = UserData(this, null, null).load()
        if (!userData.username.isNullOrEmpty() && !userData.password.isNullOrEmpty()) {
            loginViewModel.login(userData.username!!, userData.password!!, false, {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
                { }
            )
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    fun showLoading(isShow: Boolean) {
        runOnUiThread {
            loading.visibility = if (isShow) View.VISIBLE else View.GONE
        }
    }

    companion object {
        const val LOGOUT_NAV_EXTRA = "logout_nav"
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
