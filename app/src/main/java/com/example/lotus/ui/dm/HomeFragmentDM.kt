package com.example.lotus.ui.dm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.lotus.R
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.home.HomeActivity


class HomeFragmentDM : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_homedm, container, false)
        listenAppToolbar(v)

        return v
    }

    private fun listenAppToolbar(v: View?) {
        val toolbar: Toolbar = v?.findViewById(R.id.tbMessage) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
        }

        toolbar.setOnMenuItemClickListener(){
            when (it.itemId){
                R.id.fragmentNewDM ->
                    view?.findNavController()?.navigate(R.id.action_homeFragmentDM_to_newMessageFragment)

            }
            when (it.itemId){
                R.id.explore ->
                    toolbar.context.startActivity(Intent(context, GeneralActivity::class.java))
            }
            true
        }
    }
}
