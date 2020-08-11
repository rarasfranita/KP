package com.example.lotus.ui.forgotpass

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Data
import com.example.lotus.models.DataUser
import com.example.lotus.models.Respon
import com.example.lotus.models.Token
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.home.HomeActivity
import com.example.lotus.ui.home.HomeFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_new_pass_frgt_pas.*
import kotlinx.android.synthetic.main.fragment_verif_code_forgot_pass_.*

class NewPassFrgtPasFragment : Fragment() {
    var token: String? = null

    var manager: FragmentManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_pass_frgt_pas, container, false)
        val saveToken = this.arguments?.getString("token")
        token = saveToken
        Log.d("tokenVerifCode", token.toString())
        manager = activity?.supportFragmentManager
        matchPassword(view)
        return view
    }

    private fun matchPassword(v: View) {
        val toolbar: MaterialToolbar = v.findViewById(R.id.tbForgotPasswordSetPassword) as MaterialToolbar

        toolbar.setNavigationOnClickListener {
            VerifCodeForgotPassFragmentLayout.visibility = View.VISIBLE
            activity?.onBackPressed()
        }
        val pass2 = v.findViewById<EditText>(R.id.pw2FrgtPass)
        pass2.addTextChangedListener(object : TextWatcher {
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
                pass()
            }
        })

    }

    private fun pass() {
        if(pw1FrgtPass.length() < 6 && pw2FrgtPass.length() <6 ){
            pw2FrgtPass.setError("Password cannot be less than 6 characters!")
            btnNext.isEnabled=false
        }else if(pw1FrgtPass.text.toString() != pw2FrgtPass.text.toString()){
            pw2FrgtPass.error = "Password don't match"
            btnNext.isEnabled=false
        }else if(pw1FrgtPass.text.toString() == pw2FrgtPass.text.toString()){
            btnNext.isEnabled=true
        }
        btnNext.setOnClickListener {
//            val bundle = Bundle().apply {
//                putString("pass",pw2FrgtPass.text.toString())
//                Log.d("PWpwtoprofile", pw2FrgtPass.text.toString())
//            }

            val newPass = pw2FrgtPass.text.toString()
            newPassword(newPass)
        }
    }

    private fun newPassword(pass: String) {
        val load = view!!.findViewById<ProgressBar>(R.id.loading)
        load.visibility = View.VISIBLE

        Log.d("newPass", pw2FrgtPass.text.toString())

        AndroidNetworking.post(EnvService.ENV_API + "/users/forgot-password/update")
            .addBodyParameter("token", token)
            .addBodyParameter("newPassword", pass)
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

                            passwordLayout.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Reset Password Succes",
                                Toast.LENGTH_LONG
                            ).show()
                            login()
                        }else {
                            load.visibility = View.GONE
                            Log.e("ERROR!!!", "Token ${respon.code}, ${respon.data}")
                        }
                    }

                    override fun onError(anError: ANError) {
                        load.visibility = View.GONE
                        Log.e("ERROR!!!", " ${anError.errorCode}")

                    }
                })
    }

    private fun login() {
        val value = this.arguments?.getString("email")
        val newPass = pw2FrgtPass.text.toString()
        Log.d("valueeeee", value.toString())
        Log.d("NewPassssss", newPass)
        AndroidNetworking.post(EnvService.ENV_API + "/users/login")
            .addBodyParameter("key", value.toString())
            .addBodyParameter("password", newPass)
            .addBodyParameter("type", "email")
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(Respon::class.java, object : ParsedRequestListener<Respon> {
                override fun onResponse(respon: Respon) {
                    val gson = Gson()
                    if (respon.code.toString() == "200") {
                        val strRes = gson.toJson(respon.data)
                        val dataJson = gson.fromJson(strRes, DataUser::class.java)
                        val data = gson.fromJson(strRes, Token::class.java)

                        SharedPrefManager.getInstance(requireContext()).saveToken(data)
                        SharedPrefManager.getInstance(requireContext()).saveUser(dataJson.user)
                        Log.d("tokenLogin", data.toString())
                        Log.d("saveUser", dataJson.user.toString())
                        val intent = Intent(context, HomeActivity::class.java)
                        startActivity(intent)
//                        val gotoHome = HomeFragment()
//                        manager?.beginTransaction()
//                            ?.replace(R.id.navForgotPassword, gotoHome)
//                            ?.addToBackStack("ForgotPassword")
//                            ?.commit()
                    } else {
                        Log.d("onError: Failed", respon.toString())
                        Toast.makeText(
                            context,
                            "" + respon.data.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("onError: Failed", error.toString())
                    Toast.makeText(
                        context,
                        "gagal login",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

}