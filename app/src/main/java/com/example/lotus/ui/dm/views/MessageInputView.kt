package com.example.lotus.ui.dm.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.lotus.databinding.ViewMessageInputBinding
import com.getstream.sdk.chat.interfaces.MessageSendListener
import com.getstream.sdk.chat.rest.Message
import com.getstream.sdk.chat.rest.interfaces.MessageCallback
import com.getstream.sdk.chat.rest.response.MessageResponse
import com.getstream.sdk.chat.view.MessageInputView
import com.getstream.sdk.chat.viewmodel.ChannelViewModel

class MessageInputView: ConstraintLayout
{
    private lateinit var binding: ViewMessageInputBinding

    constructor(context: Context) : super(context){
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet):    super(context, attrs){
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?,    defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = ViewMessageInputBinding.inflate(inflater, this, true)
    }

    fun setViewModel(
        viewModel: ChannelViewModel,
        lifecycleOwner: LifecycleOwner?
    ) {
        binding.lifecycleOwner = lifecycleOwner
        binding.viewModel = viewModel

        // implement message sending
        binding.voiceRecordingOrSend.setOnClickListener {
            val message : Message = Message()
            message.text =  binding.messageInput.text.toString()
            viewModel.sendMessage(message, object: MessageCallback {
                override fun onSuccess(response: MessageResponse?) {
                    // hi
                    viewModel.messageInputText.value = ""
                }

                override fun onError(errMsg: String?, errCode: Int) {
                }

            })
        }

        // listen to typing events and connect to the view model
        binding.messageInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) viewModel.keystroke()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
    }

}