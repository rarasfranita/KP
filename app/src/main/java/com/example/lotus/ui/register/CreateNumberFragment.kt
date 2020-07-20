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
import kotlinx.android.synthetic.main.fragment_create_email.view.*
import kotlinx.android.synthetic.main.fragment_create_number.view.*

class CreateNumberFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val x = inflater.inflate(R.layout.fragment_create_number, container, false)
        listenAppToolbar(x)
        x.btnContinueNumber.setOnClickListener { view: View? ->
            view?.findNavController()
                ?.navigate(R.id.action_createNumberFragment2_to_verificationCodeFragment)
        }
        x.goWA.setOnClickListener { view: View? ->
            view?.findNavController()
                ?.navigate(R.id.action_createNumberFragment2_to_createEmailFragment)
        }
        return x
    }

    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbregisterWA) as Toolbar

        toolbar.setOnMenuItemClickListener(){
            when (it.itemId){
                R.id.tbregisterWA ->
                    view?.findNavController()?.navigate(R.id.action_createNumberFragment2_to_createEmailFragment)

            }
            true
        }
    }
}