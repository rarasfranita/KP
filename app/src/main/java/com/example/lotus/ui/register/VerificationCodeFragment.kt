package com.example.lotus.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.example.lotus.R
import com.example.lotus.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_create_email.view.*
import kotlinx.android.synthetic.main.fragment_verification_code.view.*

class VerificationCodeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val y = inflater.inflate(R.layout.fragment_verification_code, container, false)
        listenAppToolbar(y)
        y.btnContinueVC.setOnClickListener { view: View? ->
            view?.findNavController()
                ?.navigate(R.id.action_verificationCodeFragment_to_choseUsernameFragment2)
        }

        return y
    }

    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbregisterVC) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}