package com.example.lotus.ui.DM.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListMessageModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is message Fragment"
    }
    val text: LiveData<String> = _text
}