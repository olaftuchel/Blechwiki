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
class KomponistAdapter(var mKomponistList: List<KomponistClass>) : RecyclerView.Adapter<KomponistAdapter.KomponistViewHolder>(), Filterable {
    private var charStringOld: String? = null
    private lateinit var mKomponistListOriginal: List<KomponistClass>

    class KomponistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView
        var detailTextView1: TextView
        var detailTextView2: TextView
        fun bind(item: BuchClass) {
            titleTextView.text = item.Buch
        }

        init {
            titleTextView = itemView.findViewById<View>(R.id.komponist_list) as TextView
            detailTextView1 = itemView.findViewById<View>(R.id.komponist_list_geboren) as TextView
            detailTextView2 = itemView.findViewById<View>(R.id.komponist_list_gestorben) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomponistViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_komponist, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return KomponistViewHolder(view)
    }

    override fun onBindViewHolder(holder: KomponistViewHolder, position: Int) {
        holder.titleTextView.text = mKomponistList[position].Komponist
//        holder.detailTextView1.text = mKomponistList[position].geboren
//        holder.detailTextView2.text = mKomponistList[position].gestorben
    }

    fun setListEntries(mList: List<KomponistClass>) {
        mKomponistList = mList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mKomponistList.size
    }

    override fun getFilter(): Filter {
        Log.d(ContentValues.TAG, "KomponistAdapter (getFilter)")
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                var charString = charSequence.toString()
                charString = charString.toLowerCase(Locale.getDefault())
                if (charStringOld == null) {
                    mKomponistList = mKomponistList // filtering starts
                    charStringOld = charString
                }
                if (charString.isEmpty()) {
                    mKomponistList = mKomponistList // filter cleared
                    charStringOld = null
                } else {
                    val filteredList: MutableList<KomponistClass> = ArrayList()
                    for (row in mKomponistList) {
                        if (row.Komponist.toLowerCase(Locale.getDefault()).contains(charString)) {
                            filteredList.add(row)
                        }
                    }
                    mKomponistList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mKomponistList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                notifyDataSetChanged()
            }
        }
    }

    init {
        mKomponistList = mKomponistList
    }
}