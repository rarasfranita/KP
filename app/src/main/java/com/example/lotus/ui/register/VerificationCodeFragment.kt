package com.example.lotus.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
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
import com.example.lotus.ui.login.LoginActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_create_email.*
import kotlinx.android.synthetic.main.fragment_verification_code.*
import kotlinx.android.synthetic.main.fragment_verification_code.view.*
import org.jetbrains.anko.support.v4.email

class VerificationCodeFragment : Fragment() {

    var token: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        token = SharedPrefManager.getInstance(requireActivity()).token.token
        val view = inflater.inflate(R.layout.fragment_verification_code, container, false)

        view.tbVerCode.setOnClickListener {
            SharedPrefManager.getInstance(requireContext()).saveState("")
            findNavController()
            ?.navigate(R.id.action_verificationCodeFragment_to_createEmailFragment)
        }
        listenAppToolbar(view)
        listenBtnVerifCode(view)
        sendCodeVerifCode(view)
        SharedPrefManager.getInstance(requireContext()).saveState("vercode")
        return view
    }

    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbVerCode) as Toolbar
        val verCode : EditText = v.findViewById(R.id.etVerifCode)

        verCode.addTextChangedListener(object : TextWatcher {
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
              btnContinueVC.isEnabled=true
            }
        })

    }

    fun listenBtnVerifCode(v:View){
        val btnNext = v.btnContinueVC
        val vercod = v.etVerifCode

        btnNext.setOnClickListener {
           val strCode = vercod.text.toString()
            Log.d("ini code verif",strCode)
            checkCode(strCode)
        }
    }

    fun sendCodeVerifCode(v: View){
        val sendCode = v.tvSendCode

        sendCode.setOnClickListener {
            val strCode2 = sendCode.text.toString()
            Log.d("ini coba send code",strCode2)
            }
    }

    fun checkCode (code: String){
        AndroidNetworking.get(EnvService.ENV_API + "/users/verify-registration-code?code=$code&token=$token")
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
                            if (data.status.toString() == "OK" ){
                                SharedPrefManager.getInstance(requireContext()).saveToken(Token(data.token.toString()))
                                findNavController()
                                    ?.navigate(R.id.action_verificationCodeFragment_to_choseUsernameFragment)
                                Log.d("verif code berhasil atau tidak","${data.status}, dan token: ${data.token}")
                            }else{
                                Log.d("Else", data.status.toString())
                                Toast.makeText(context,"Your code is incorrect, please check again", Toast.LENGTH_LONG).show()
                             }
                        }else {
                            Log.e("ERROR!!!", "Cek code  ${respon.code}, ${respon.data}")
                        }
                    }
                    override fun onError(anError: ANError) {
                        Log.e("ERROR!!!", "Cek code ${anError.errorCode}")
                    }
                })

    }

    fun register (destination: String){
        AndroidNetworking.post(EnvService.ENV_API + "/users/register")
            .addBodyParameter("method", "email")
            .addBodyParameter("destination",destination)
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
                            SharedPrefManager.getInstance(requireContext()).saveToken(Token(data.token.toString()))
                        }else {
                            Log.e("ERROR!!!", "Token ${respon.code}, ${respon.data}")
                        }
                    }

                    override fun onError(anError: ANError) {
                        Log.e("ERROR!!!", "Token ${anError.errorCode}")
                    }
                })
    }

}