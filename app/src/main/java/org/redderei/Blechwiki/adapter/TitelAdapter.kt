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



class TitelAdapter(var mTitelList: List<TitelClass>) : RecyclerView.Adapter<TitelViewHolder>(), Filterable {
    private var charStringOld: String? = null
    private lateinit var mTitelListOriginal: List<TitelClass>

    class TitelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView
        fun bind(item: BuchClass) {
            titleTextView.text = item.Buch
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
        holder.titleTextView.text = mTitelList[position].Titel
    }

    fun setListEntries(mList: List<TitelClass>) {
        mTitelList = mList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mTitelList.size
    }

    override fun getFilter(): Filter {
        Log.d(ContentValues.TAG, "TitelAdapter (getFilter)")
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                var charString = charSequence.toString()
                charString = charString.toLowerCase(Locale.getDefault())
                if (charStringOld == null) {
                    mTitelList = mTitelList // filtering starts
                    charStringOld = charString
                }
                if (charString.isEmpty()) {
                    mTitelList = mTitelList // filter cleared
                    charStringOld = null
                } else {
                    val filteredList: MutableList<TitelClass> = ArrayList()
                    for (row in mTitelList) {
                        if (row.Titel.toLowerCase(Locale.getDefault()).contains(charString)) {
                            filteredList.add(row)
                        }
                    }
                    mTitelList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mTitelList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                notifyDataSetChanged()
            }
        }
    }

    init {
        mTitelList = mTitelList
    }
}