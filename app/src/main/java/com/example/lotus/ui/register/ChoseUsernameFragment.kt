package com.example.lotus.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.lotus.R
import kotlinx.android.synthetic.main.fragment_chose_username.view.*
import kotlinx.android.synthetic.main.fragment_verification_code.view.*


class ChoseUsernameFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val z = inflater.inflate(R.layout.fragment_chose_username, container, false)
        z.btnContinueChoseUname.setOnClickListener { view: View? ->
            view?.findNavController()
                ?.navigate(R.id.action_choseUsernameFragment_to_passwordFragment)
        }

        return z
    }


}