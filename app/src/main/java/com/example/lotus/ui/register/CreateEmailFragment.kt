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
import com.example.lotus.ui.home.HomeActivity
import com.example.lotus.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_create_email.view.*


class CreateEmailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val e = inflater.inflate(R.layout.fragment_create_email, container, false)
        listenAppToolbar(e)
        e.btnContinueEmail.setOnClickListener { view: View? ->
            view?.findNavController()
                ?.navigate(R.id.action_createEmailFragment_to_verificationCodeFragment)
        }
        e.goWA.setOnClickListener { view: View? ->
            view?.findNavController()
                ?.navigate(R.id.action_createEmailFragment_to_createNumberFragment2)
        }
        return e
    }

    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbregisterEmail) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}