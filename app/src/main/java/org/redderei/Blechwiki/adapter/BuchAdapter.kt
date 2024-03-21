package org.redderei.Blechwiki.adapter

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.redderei.Blechwiki.R
import org.redderei.Blechwiki.gettersetter.BuchClass
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ot775x on 03.03.2018.
 * shows up titel_in_buch_listl
 * https://www.raywenderlich.com/124438/android-listview-tutorial
 */
class BuchAdapter(var mBuchList: List<BuchClass>) : RecyclerView.Adapter<BuchAdapter.BuchViewHolder>(), Filterable {
    private var charStringOld: String? = null
    private lateinit var mBuchListOriginal: List<BuchClass>     //written at first usage, before filtering starts in getFilter

    class BuchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var liedTextView: TextView
        var titleTextView: TextView
        var subtitleTextView: TextView
        var detailTextView: TextView
        var thumbnailImageView: ImageView
        fun bind(item: BuchClass) {
            titleTextView.text = item.Buch
        }

        init {
// LiedTextView is nonsense here
//            liedTextView = itemView.findViewById<View>(R.id.titel_eglied) as TextView
            titleTextView = itemView.findViewById<View>(R.id.titel_main) as TextView
            subtitleTextView = itemView.findViewById<View>(R.id.title_sub1) as TextView
            detailTextView = itemView.findViewById<View>(R.id.buch_list_nr) as TextView
            // Get thumbnail element 40*60px
            thumbnailImageView = itemView.findViewById<View>(R.id.buch_list_thumbnail) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuchViewHolder {
        Log.v(TAG, "BuchAdapter (onCreateViewHolder)");
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_buch, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return BuchViewHolder(view)
    }

    override fun onBindViewHolder(holder: BuchViewHolder, position: Int) {
        Log.v(TAG, "BuchAdapter (onBindViewHolder)");
        holder.titleTextView.text = mBuchList[position].Buch
        holder.subtitleTextView.text = mBuchList[position].Untertitel
//        holder.detailTextView.text = mBuchList[position].erscheinjahr
        //this would be normal varian for imgUrl
//        Picasso.get().load(mBuchList[position].imgUrl).placeholder(R.drawable.keinbild).into(holder.thumbnailImageView)
        Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Latin_dictionary.jpg/275px-Latin_dictionary.jpg").placeholder(R.drawable.keinbild).into(holder.thumbnailImageView)
    }

    fun setListEntries(mList: List<BuchClass>) {
        Log.d(ContentValues.TAG, "BuchAdapter (setListEntries)")
        mBuchList = mList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "BuchAdapter (getItemCount) " + mBuchList.size);
        return mBuchList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                var charString = charSequence.toString()
                charString = charString.toLowerCase(Locale.getDefault())
                if (charStringOld == null) {
                    mBuchListOriginal = mBuchList // filtering starts
                    charStringOld = charString
                }
                if (charString.isEmpty()) {
                    mBuchList = mBuchListOriginal // filter cleared
                    charStringOld = null
                } else {
                    val filteredList: MutableList<BuchClass> = ArrayList()
                    for (row in mBuchList) {
                        if (row.Buch.toLowerCase(Locale.getDefault()).contains(charString)) {
                            filteredList.add(row)
                        }
                    }
                    mBuchList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mBuchList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                // refresh the list with filtered data
                notifyDataSetChanged()
            }
        }
    }

    init {
        mBuchList = mBuchList
    }
}