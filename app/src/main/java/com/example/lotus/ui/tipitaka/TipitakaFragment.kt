package com.example.lotus.ui.tipitaka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.lotus.R

class TipitakaFragment : Fragment() {

    private lateinit var tipitakaViewModel: TipitakaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tipitakaViewModel =
            ViewModelProviders.of(this).get(TipitakaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tipitaka, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
        tipitakaViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
        })
        return root
    }
}