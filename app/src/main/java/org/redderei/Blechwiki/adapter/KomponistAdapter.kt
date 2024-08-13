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
import org.redderei.Blechwiki.gettersetter.BuchClass
import org.redderei.Blechwiki.gettersetter.KomponistClass
import java.util.*

/**
 * Created by ot775x on 03.03.2018, changed to RestAPI 233.10.2022
 */
class KomponistAdapter(var mList: List<KomponistClass>) : RecyclerView.Adapter<KomponistAdapter.KomponistViewHolder>(), Filterable {
    private var charStringOld: String? = null
    private lateinit var mListOriginal: List<KomponistClass>

    class KomponistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView
        fun bind(item: KomponistClass) {
            titleTextView.text = item.komponist
        }

        init {
            titleTextView = itemView.findViewById<View>(R.id.komponist_list) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomponistViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_komponist, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return KomponistViewHolder(view)
    }

    override fun onBindViewHolder(holder: KomponistViewHolder, position: Int) {
        holder.titleTextView.text = mList[position].komponist
//        holder.detailTextView1.text = mList[position].geboren
//        holder.detailTextView2.text = mList[position].gestorben
    }

    fun setListEntries(mListEntries: List<KomponistClass>) {
        mList = mListEntries
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getFilter(): Filter {
        Log.d(ContentValues.TAG, "KomponistAdapter (getFilter)")
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
                    val filteredList: MutableList<KomponistClass> = ArrayList()
                    for (row in mListOriginal) {
                        if (row.komponist.lowercase(Locale.getDefault()).contains(charString)) {
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