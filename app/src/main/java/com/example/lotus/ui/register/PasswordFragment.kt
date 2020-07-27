package com.example.lotus.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.lotus.R
import kotlinx.android.synthetic.main.fragment_chose_username.view.*
import kotlinx.android.synthetic.main.fragment_password.view.*

class PasswordFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val p = inflater.inflate(R.layout.fragment_password, container, false)
        p.btnContinueSetPassword.setOnClickListener { view: View? ->
            view?.findNavController()
                ?.navigate(R.id.action_passwordFragment_to_profileDataFragment)
        }

        return p
    }

}