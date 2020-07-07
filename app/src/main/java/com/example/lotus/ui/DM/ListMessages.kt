package com.example.lotus.ui.DM

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.lotus.R
import com.example.lotus.ui.home.HomeActivity
import com.getstream.sdk.chat.StreamChat
import com.getstream.sdk.chat.adapter.ChannelListItemAdapter
import com.getstream.sdk.chat.enums.Filters
import com.getstream.sdk.chat.rest.User
import com.getstream.sdk.chat.viewmodel.ChannelListViewModel

const val API_KEY = "s2dxdhpxd94g"
const val USER_ID = "empty-queen-5"
const val USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiZW1wdHktcXVlZW4tNSJ9.RJw-XeaPnUBKbbh71rV1bYAKXp6YaPARh68O08oRnOU"

class ListMessages : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // we're using data binding in this example
        val v = inflater.inflate(R.layout.fragment_list_messages, container, false)
        listenAppToolbar(v)

        return v
    }
    private fun listenAppToolbar(v: View){
        val toolbar: Toolbar = v.findViewById(R.id.tbMessage) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
        }

        toolbar.setOnMenuItemClickListener(){
            when (it.itemId){
                R.id.fragmentNewDM ->
                    view?.findNavController()
                        ?.navigate(R.id.action_listMessages_to_newMessageFragment)
//                    toolbar.context.startActivity(Intent(context, MainActivityDM::class.java))
            }
            true
        }
    }

}

