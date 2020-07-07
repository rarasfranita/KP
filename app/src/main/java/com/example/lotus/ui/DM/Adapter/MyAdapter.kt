package com.example.lotus.ui.DM.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lotus.R
import com.example.lotus.ui.DM.Model.Item

class MyAdapter (internal var context: Context,
                internal var itemList:List<Item>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() , Filterable {

    internal var filterListResult : List<Item>

    init {
        this.filterListResult = itemList
    }

    inner class MyViewHolder (itemView:View) :RecyclerView.ViewHolder(itemView) {
        internal var txt_title:TextView
        internal var txt_url:TextView

        init {
            txt_title = itemView.findViewById(R.id.username)
            txt_url = itemView.findViewById(R.id.message_last)
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.message_item_layout,p0,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return filterListResult.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txt_title.text = filterListResult.get(position).title
        holder.txt_url.text = filterListResult.get(position).url
    }

    override fun getFilter(): Filter {
        return object:Filter(){
            override fun performFiltering(charString: CharSequence?): FilterResults {
                val charSearch = charString.toString()
                if (charSearch.isEmpty())
                    filterListResult = itemList
                else
                {
                    val resultList = ArrayList<Item>()
                    for (row in itemList)
                    {
                        if (row.title!!.toLowerCase().contains(charSearch.toLowerCase()))
                            resultList.add(row)
                    }
                    filterListResult = resultList
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = filterListResult
                return filterResults
            }

            override fun publishResults(charSquence: CharSequence?, filterResults: FilterResults?) {
                filterListResult = filterResults!!.values as List<Item>
                notifyDataSetChanged()
            }

        }
    }

}