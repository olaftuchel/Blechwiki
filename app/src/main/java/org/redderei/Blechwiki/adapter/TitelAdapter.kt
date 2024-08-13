package org.redderei.Blechwiki.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.redderei.Blechwiki.R
import org.redderei.Blechwiki.adapter.TitelAdapter.TitelViewHolder
import org.redderei.Blechwiki.gettersetter.BuchClass
import org.redderei.Blechwiki.gettersetter.TitelClass
import java.util.*



class TitelAdapter(var mList: List<TitelClass>) : RecyclerView.Adapter<TitelAdapter.TitelViewHolder>(), Filterable {
    private var charStringOld: String? = null
    private lateinit var mListOriginal: List<TitelClass>

    class TitelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView
        fun bind(item: BuchClass) {
            titleTextView.text = item.buch
        }

        init {
            titleTextView = itemView.findViewById<View>(R.id.titel_list_detail) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_titel, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return TitelViewHolder(view)
    }

    override fun onBindViewHolder(holder: TitelViewHolder, position: Int) {
        holder.titleTextView.text = mList[position].titel
    }

    fun setListEntries(mListEntries: List<TitelClass>) {
        mList = mListEntries
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getFilter(): Filter {
        Log.d(ContentValues.TAG, "TitelAdapter (getFilter)")
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                var charString = charSequence.toString()
                charString = charString.lowercase(Locale.getDefault())
                if (charStringOld == null) {
                    mListOriginal = mList // filtering starts
                    charStringOld = charString
                }
                if (charString.isEmpty()) {
                    mList = mListOriginal // filter cleared
                    charStringOld = null
                } else {
                    val filteredList: MutableList<TitelClass> = ArrayList()
                    for (row in mListOriginal) {
                        if (row.titel.lowercase(Locale.getDefault()).contains(charString)) {
                            filteredList.add(row)
                        }
                    }
                    mList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                notifyDataSetChanged()
            }
        }
    }
}