package com.example.lotus.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
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
import kotlinx.android.synthetic.main.fragment_create_email.view.*
import org.jetbrains.anko.support.v4.toast


class CreateEmailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_create_email, container, false)

        view.tvGoWhatsapp.setOnClickListener {
            findNavController()
                ?.navigate(R.id.action_createEmailFragment_to_createNumberFragment)
        }
        listenAppToolbar(view)

        listenBtnCreateEmail(view)
        return view

    }

    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbregisterEmail) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

    }


    fun listenBtnCreateEmail (v: View){
        val btnNext = v.btnContinueEmail
        val textEmail = v.etEmail

        btnNext.setOnClickListener {
            val strEmail = textEmail.text.toString()
            Log.d("email", strEmail)
            checkEmail(strEmail)
        }
    }

    fun checkEmail (email: String){
        AndroidNetworking.get(EnvService.ENV_API + "/users/check?email=$email")
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
                            Log.d("Cek Email", "Success")
                            Log.d("Status", data.status.toString())
                            if (data.status.toString() == "AVAILABLE" ){
                                register(email.toString())
                            }else{
                                Log.d("Else", data.status.toString())
                                Toast.makeText(context,"Email is already taken", Toast.LENGTH_LONG).show()
                            }
                        }else {
                            Log.e("ERROR!!!", "Cek Email ${respon.code}, ${respon.data}")
                        }
                    }
                    override fun onError(anError: ANError) {
                        Log.e("ERROR!!!", "Cek Email ${anError.errorCode}")
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
                            view?.findNavController()
                                ?.navigate(R.id.action_createEmailFragment_to_verificationCodeFragment)
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