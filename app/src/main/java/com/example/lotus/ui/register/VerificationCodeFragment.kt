package com.example.lotus.ui.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_verification_code.view.*

class VerificationCodeFragment : Fragment() {
    // get token masukin variable
    var token: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //get token
        token = SharedPrefManager.getInstance(requireActivity()).token.token
        val view = inflater.inflate(R.layout.fragment_verification_code, container, false)

        view.tbregisterVC.setOnClickListener {
            SharedPrefManager.getInstance(requireContext()).saveState("")
            findNavController()
                ?.navigate(R.id.action_verificationCodeFragment_to_createEmailFragment)
        }

        listenBtnVerifCode(view)
        SharedPrefManager.getInstance(requireContext()).saveState("vercode")
        return view
    }


    // fungsi listen button click
    // 1. listen oncllick

    fun listenBtnVerifCode(v:View){
        val btnNext = v.btnContinueVC
        val vercod = v.etVerifCode

        // 2. get string from edittext
        btnNext.setOnClickListener {
           val strCode = vercod.text.toString()
            Log.d("ini code verif",strCode)
            checkCode(strCode)
        }
    }

    // 3. send to verifCode fun
    // fungsi verifcode
    //hit api dengan parameter code & token
    fun checkCode (code: String){
        Log.d("TOKEN NIH", token.toString())
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
                                view?.findNavController()
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

    //code udh ke verif pindah page ke username terus simpan state (update state)

}