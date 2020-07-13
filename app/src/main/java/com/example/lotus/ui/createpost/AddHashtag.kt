package com.example.lotus.ui.createpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.lotus.R
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.fragment_add_hashtag.*
import java.util.*

class AddHashtag (private val callbackListener: CallbackListener) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false

        val view = inflater.inflate(R.layout.fragment_add_hashtag, container, false)

        val bundle = arguments
        if (bundle != null){
            val textAdd = view.findViewById<EditText>(R.id.textAddHashtag)
            setPreviousTags(textAdd, bundle.getStringArrayList("Tag"))
        }

        return view
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        icSaveTag.setOnClickListener {
            val sentence = textAddHashtag.text.toString().replace("\\s".toRegex(), "")
            callbackListener.onDataReceived(sentence)
            dismiss()
        }

        val toolbar = view.findViewById<MaterialToolbar>(R.id.tbCreatePost)
        toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    fun setPreviousTags(tagView: EditText, tags: ArrayList<String>?){
        var strTags = ""
        if (tags != null) {
            for ((i,tag) in tags.withIndex()){
                if (i < tags.size - 1){
                    strTags += "$tag,"
                }else
                    strTags += tag
            }
        }

        tagView.setText(strTags)
    }
}