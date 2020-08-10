package com.example.lotus.ui.forgotpass

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Data
import com.example.lotus.models.Respon
import com.example.lotus.service.EnvService
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_verif_code_forgot_pass_.*
import kotlinx.android.synthetic.main.fragment_verif_code_forgot_pass_.view.*

class VerifCodeForgotPassFragment : Fragment() {
    var manager: FragmentManager? = null
    var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_verif_code_forgot_pass_, container, false)
        val saveToken = this.arguments?.getString("token")
        token = saveToken
        Log.d("tokenAwalFrgtPass", saveToken.toString())

        manager = activity?.supportFragmentManager
        listenBtnVerifCode(view)
        btnVerifCode(view)
        visibleLinlay(view)
//        sendCodeAgain(view)
        listenAppToolbar(view)

        return view
    }

    private fun listenAppToolbar(v: View) {
        val toolbar: MaterialToolbar = v.findViewById(R.id.tbVerCodeFrgtPass) as MaterialToolbar

        toolbar.setNavigationOnClickListener {
            resetYourPass.visibility = View.VISIBLE
            activity?.onBackPressed()
        }
    }
    private fun listenBtnVerifCode(v: View) {
        val verCode: EditText = v.findViewById(R.id.etVerifCode_FrgtPass)
        verCode.addTextChangedListener(object : TextWatcher {
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
                btnContinueVC_FrgtPass.isEnabled = true
            }
        })

    }

    private fun btnVerifCode(v: View) {
        val btnNext = v.btnContinueVC_FrgtPass
        val vercod = v.etVerifCode_FrgtPass

        btnNext.setOnClickListener {
            val strCode = vercod.text.toString()
            checkCode(strCode)
        }

    }

    private fun visibleLinlay(v: View) {
        val value = this.arguments?.getString("email")
        val email = v.findViewById<TextView>(R.id.tvEmailFrgtPass)

        if (value != null) {
            email.text = value
            Log.d("email", email.text.toString())
        }
    }

    private fun sendCodeAgain(v: View) {

    }

    private fun checkCode(code: String) {
        val value = this.arguments?.getString("email")
        val load = view!!.findViewById<ProgressBar>(R.id.loading)
        load.visibility = View.VISIBLE
        Log.d("code", code)
        Log.d("tokencheckCode", token.toString())
        AndroidNetworking.get(EnvService.ENV_API + "/users/forgot-password/verify")
            .addQueryParameter("code", code)
            .addQueryParameter("token", token)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(respon: Respon) {
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            load.visibility = View.GONE
                            val dataJson = gson.toJson(respon.data)
                            val data = gson.fromJson(dataJson, Data::class.java)
                            val bundle = Bundle().apply {
                                putString("email", value)
                                Log.d("emailValue", value.toString())
                                putString("token", data.token.toString())
                                Log.d("tokenVerifCode", data.token.toString())
                            }
                            VerifCodeForgotPassFragmentLayout.visibility = View.GONE
                            val gotoNewPass = NewPassFrgtPasFragment()
                            gotoNewPass.arguments = bundle
                            manager?.beginTransaction()
                                ?.replace(R.id.navForgotPassword, gotoNewPass)
                                ?.addToBackStack("ForgotPassword")
                                ?.commit()
                        } else {
                            load.visibility = View.GONE
                            Log.e("ERROR!!!", "Cek code  ${respon.code}, ${respon.data}")
                        }
                    }

                    override fun onError(anError: ANError) {
                        load.visibility = View.GONE
                        Log.e("ERROR!!!!", "Cek code ${anError.errorCode}")
                    }
                })

    }

}