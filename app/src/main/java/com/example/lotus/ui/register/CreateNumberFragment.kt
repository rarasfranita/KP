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
import kotlinx.android.synthetic.main.fragment_create_number.view.*

class CreateNumberFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_create_number, container, false)
        listenAppToolbar(view)

        view.btnContinueNumber.setOnClickListener {
            view?.findNavController()
                ?.navigate(R.id.action_createNumberFragment_to_verificationCodeFragment)
        }
        view.goEmail.setOnClickListener {
            view?.findNavController()
                ?.navigate(R.id.action_createNumberFragment_to_createEmailFragment)
        }
        return view
    }

    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbregisterWA) as Toolbar
            toolbar.setNavigationOnClickListener {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
