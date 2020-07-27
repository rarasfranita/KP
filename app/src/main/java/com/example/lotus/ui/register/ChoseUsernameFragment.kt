package com.example.lotus.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
import com.example.lotus.ui.login.afterTextChanged
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_chose_username.*
import kotlinx.android.synthetic.main.fragment_chose_username.view.*


class ChoseUsernameFragment : Fragment() {
    private var USERNAME_PATTERN =
    "^[a-zA-Z](_(?!(\\.|_))|\\.(?!(_|\\.))|[a-zA-Z0-9]){4,11}[a-zA-Z0-9]\$"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chose_username, container, false)

        listenBtnUsername(view)
        SharedPrefManager.getInstance(requireContext()).saveState("chosename")

        val usernameInput = view.etUsername
        usernameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }    override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }    override fun afterTextChanged(editable: Editable) {
                usernameInput.afterTextChanged {
                    btnChoseUname.isEnabled=validateUsername(usernameInput, usernameInput.text.toString())

                }
            }
        })

        return view
    }

    fun listenBtnUsername (v: View){
        val btnNext = v.btnChoseUname
        val textUname = v.etUsername

        btnNext.setOnClickListener {
            val strEmail = textUname.text.toString()
            Log.d("Username", strEmail)
            checkUsername(strEmail)
        }
    }


    private fun validateUsername(v: EditText, username: String): Boolean {

        return if (username.isEmpty()) {
            v.setError("Username can't be empty")
            false
        } else if (!username.matches(USERNAME_PATTERN.toRegex())) {
            Log.d("CEK YA", "")
            v.setError("Username is not available")
            false
        } else {
            Log.d("CEK YA", "else")
            v.etUsername.setError(null)
            true
        }
    }


    fun checkUsername (username: String){
        AndroidNetworking.get(EnvService.ENV_API + "/users/check?username=$username")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(respon: Respon) {
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            val dataJson =gson.toJson(respon.data)
                            val data = gson.fromJson(dataJson, Data::class.java)
                            Log.d("Cek Username", "Success")
                            Log.d("Status", data.status.toString())
                            if (data.status.toString() == "AVAILABLE" ){
                                SharedPrefManager.getInstance(requireContext()).saveToken(Token(data.token.toString()))
                                view?.let { Navigation.findNavController(it).navigate(R.id.action_choseUsernameFragment_to_passwordFragment) }
//                                findNavController()
//                                    ?.navigate(R.id.action_choseUsernameFragment_to_passwordFragment)
                            }else{
                                Log.d("Else", data.status.toString())
                                Toast.makeText(context,"Username is already taken", Toast.LENGTH_LONG).show()
                            }
                        }else {
                            Log.e("ERROR!!!", "Cek Username ${respon.code}, ${respon.data}")
                        }
                    }
                    override fun onError(anError: ANError) {
                        Log.e("ERROR!!!", "Cek Username ${anError.errorCode}")
                    }
                })
    }





}