package com.example.lotus.ui.forgotpass

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Data
import com.example.lotus.models.Respon
import com.example.lotus.models.Token
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_forgot_password.view.*
import java.util.regex.Pattern

class ForgotPasswordFragment : Fragment() {
    val PHONE_PATTERN: Pattern =
        Pattern.compile("(\\+62 ((\\d{3}([ -]\\d{3,})([- ]\\d{4,})?)|(\\d+)))|(\\(\\d+\\) \\d+)|\\d{3}( \\d+)+|(\\d+[ -]\\d+)|\\d+")
    var manager: FragmentManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_forgot_password, container, false)

        manager = activity!!.supportFragmentManager
        listenAppToolbar(view)
        listenBtnCreateEmail(view)
        return view
    }
    fun listenAppToolbar(v: View) {
        val toolbar: MaterialToolbar = v.findViewById(R.id.tbForgotPassword) as MaterialToolbar
        val input : EditText = v.findViewById(R.id.etInput)

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                input()
            }
        })

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }
    fun input() {
//        when {
//            Patterns.EMAIL_ADDRESS.matcher(etInput.text.toString()).matches() -> {
//                btnContinue.isEnabled
//            }
//            PHONE_PATTERN.matcher(etInput.text.toString()).matches() -> {
//                btnContinue.isEnabled
//            }
//        }
        btnContinue.isEnabled = Patterns.EMAIL_ADDRESS.matcher(etInput.text.toString()).matches()
    }

    private fun listenBtnCreateEmail(v: View) {
        val btnNext = v.btnContinue
        val textEmail = v.etInput

        btnNext.setOnClickListener {
            val strEmail = textEmail.text.toString()
            Log.d("email", strEmail)
            forgotPass(strEmail)
        }
    }

    fun forgotPass (address: String){
        val load = view!!.findViewById<ProgressBar>(R.id.loading)
        load.visibility = View.VISIBLE
        val method =
            when {
                Patterns.EMAIL_ADDRESS.matcher(etInput.text.toString()).matches() -> {
                    "email"
                }
                else -> {
                    "wa"
                }
            }
        Log.d("method", method)
        AndroidNetworking.post(EnvService.ENV_API + "/users/forgot-password")
            .addBodyParameter("method", method)
            .addBodyParameter("address", address)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(respon: Respon) {
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            load.visibility = View.GONE
                            val dataJson =gson.toJson(respon.data)
                            val data = gson.fromJson(dataJson, Data::class.java)
                            val bundle = Bundle().apply {
                                putString("token", data.token.toString())
                                putString("email", etInput.text.toString())
                            }
                            resetYourPass.visibility = View.GONE
                            val move = VerifCodeForgotPassFragment()
                            move.arguments = bundle
                            manager!!.beginTransaction()
                                .replace(R.id.navForgotPassword, move)
                                .addToBackStack("ForgotPassword")
                                .commit()
                            Log.d("tokenfrgtpass", data.token.toString())

//                            SharedPrefManager.getInstance(requireContext()).saveToken(Token(data.token.toString()))
//                            Log.d("tokenfrgtpass", data.token.toString())
                        }else {
                            load.visibility = View.GONE
                            Log.e("ERROR!!!", "Token ${respon.code}, ${respon.data}")
                        }
                    }

                    override fun onError(anError: ANError) {
                        load.visibility = View.GONE
                        Log.e("ERROR!!!", "Token ${anError.errorCode}")

                    }
                })
    }
}