package com.example.lotus.ui.dm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.example.lotus.R

class NewMessageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_new_message, container, false)
        listenAppToolbar(v)

        return v
    }

    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbNewMessage) as Toolbar

        toolbar.setNavigationOnClickListener {
            view?.findNavController()?.navigate(R.id.action_newMessageFragment_to_homeFragmentDM)
        }

        toolbar.setOnMenuItemClickListener() {
            true
        }
    }
}
