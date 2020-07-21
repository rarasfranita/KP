package com.example.lotus.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.DataUser
import com.example.lotus.models.Respon
import com.example.lotus.models.Token
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.home.HomeActivity
import com.example.lotus.ui.register.RegisterActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Register.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
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

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }


            login.setOnClickListener {

                val type =
                    if (EMAIL_ADDRESS.matcher(username.text.toString()).matches()) {
                        "email"
                    } else {
                        "username"
                    }
                loading.visibility = View.VISIBLE
                AndroidNetworking.post(EnvService.ENV_API + "/users/login")
                    .addBodyParameter("key", username.text.toString())
                    .addBodyParameter("password", password.text.toString())
                    .addBodyParameter("type", type)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsObject(Respon::class.java, object : ParsedRequestListener<Respon> {
                        override fun onResponse(respon: Respon) {
                            val gson = Gson()
                            if (respon.code.toString() == "200") {
                                loading.visibility = View.INVISIBLE
                                val strRes = gson.toJson(respon.data)
                                val dataJson = gson.fromJson(strRes, DataUser::class.java)
                                val data = gson.fromJson(strRes, Token::class.java)

                                loginViewModel.login(dataJson.user.name.toString(), password.text.toString())

                                SharedPrefManager.getInstance(applicationContext).saveUser(dataJson.user)
                                SharedPrefManager.getInstance(applicationContext).saveToken(data)
                                val token = data.toString()

                                val intent = Intent(applicationContext, GeneralActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            } else {
                                Log.d("onError: Failed", respon.toString())
                                Toast.makeText(applicationContext, "" + respon.data.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onError(error: ANError) {
                            loading.visibility = View.INVISIBLE
                            Log.d("onError: Failed", error.toString())
                            Toast.makeText(applicationContext, "gagal login", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

        Log.d("Update", "Test")

        startActivity(Intent(this, HomeActivity::class.java))

        // TODO : initiate successful logged in experience
//        Toast.makeText(
//            applicationContext,
//            "$welcome $displayName",
//            Toast.LENGTH_LONG
//        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
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