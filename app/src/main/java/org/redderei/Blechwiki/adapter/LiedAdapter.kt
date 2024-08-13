package org.redderei.Blechwiki.adapter

import android.content.ContentValues
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import org.redderei.Blechwiki.MainActivity
import org.redderei.Blechwiki.R
import org.redderei.Blechwiki.gettersetter.*
import org.redderei.Blechwiki.util.*
import java.util.*

/**
 * Created by ot775x on 03.03.2018.
 */
// RecyclerView implemented according to https://www.androidhive.info/2016/01/android-working-with-recycler-view/
// https://www.journaldev.com/12478/android-searchview-example-tutorial using SearchView and Filterable
class LiedAdapter(var mList: List<LiedClass>) : RecyclerView.Adapter<LiedAdapter.LiedViewHolder>(), Filterable {
    private var charStringOld: String? = null
    private lateinit var mListOriginal: List<LiedClass>
    private val sharedPreference: SharedPreference = SharedPreference(MainActivity.appContext)

    class LiedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView
        var subtitleTextView: TextView? = null
        var detailTextView: TextView
        var anlassTextView: TextView
        fun bind(item: LiedClass) {
            titleTextView.text = item.lied
        }

        init {
            titleTextView = itemView.findViewById<View>(R.id.lied_list_lied) as TextView
            //            subtitleTextView = (TextView) itemView.findViewById(R.id.lied_list_teil);
            detailTextView = itemView.findViewById<View>(R.id.lied_list_nr) as TextView
            anlassTextView = itemView.findViewById<View>(R.id.lied_anlass) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiedViewHolder {
        // Log.v("LiedAdapter", "onCreateViewHolder");
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_lied, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return LiedViewHolder(view)
    }

    override fun onBindViewHolder(holder: LiedViewHolder, position: Int) {
        // Log.v("LiedAdapter", "onBindViewHolder");
        holder.titleTextView.text = mList[position].lied
        //            holder.subtitleTextView.setText(mList.get(position).getTeil());
        holder.detailTextView.text = mList[position].nr
        val sortType = sharedPreference.getValueString(Constant.PREF_SORTTYPE).toString()
        if (sortType === "Anlass") {
            holder.anlassTextView.visibility = View.VISIBLE
            holder.anlassTextView.text = mList[position].anlass
        } else {
            holder.anlassTextView.visibility = View.GONE
        }
    }

    fun setListEntries(mListEntries: List<LiedClass>) {
        Log.d("LiedAdapter", "setListEntries")
        mList = mListEntries
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
//        Log.d("LiedAdapter", "getItemCount");
        return mList.size
    }

    override fun getFilter(): Filter {
        Log.d("LiedAdapter", "getFilter")
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charStringOld == null) {
                    mListOriginal = mList // filtering starts
                    charStringOld = charString
                }
                if (charString.isEmpty()) {
                    mList = mListOriginal // filter cleared
                    charStringOld = null
                } else {
                    val filteredList: MutableList<LiedClass> = ArrayList()
                    for (row in mListOriginal) {
                        if (row.lied.lowercase(Locale.getDefault()).contains(charString)) {
                            filteredList.add(row)
                        }
                    }
                    mList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
//                mList = (ArrayList<LiedClass>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged()
            }
        }
    }
}