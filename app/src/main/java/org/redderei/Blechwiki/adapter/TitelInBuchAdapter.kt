package org.redderei.Blechwiki.adapter

import android.content.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.redderei.Blechwiki.R
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import java.util.*

/**
 * Created by ot775x on 03.03.2018.
 * https://www.raywenderlich.com/124438/android-listview-tutorial
 */
class TitelInBuchAdapter(private val mContext: Context, var mList: List<TitelInBuchClass>) : RecyclerView.Adapter<TitelInBuchAdapter.TitelInBuchViewHolder>(), Filterable {
    private lateinit var mListOriginal: List<TitelInBuchClass>

    class TitelInBuchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleEGLiedTextView: TextView
        var titleMainTextView: TextView
        var playView: ImageView
        var titleSub1TextView: TextView
        var titleSub2TextView: TextView
        var detailTextViewNr: TextView
        var detailTextViewBesetzung: TextView
        var detailTextViewVorzeichen: TextView
        var thumbnailImageView: ImageView

        init {

//            Log.v(TAG, "TitelInBuchViewHolder start");
            titleEGLiedTextView = itemView.findViewById<View>(R.id.titel_eglied) as TextView
            titleMainTextView = itemView.findViewById<View>(R.id.titel_main) as TextView
            playView = itemView.findViewById<View>(R.id.play) as ImageView
            titleSub1TextView = itemView.findViewById<View>(R.id.titel_sub1) as TextView
            titleSub2TextView = itemView.findViewById<View>(R.id.titel_sub2) as TextView
            detailTextViewNr = itemView.findViewById<View>(R.id.buch_list_nr) as TextView
            detailTextViewBesetzung = itemView.findViewById<View>(R.id.buch_list_besetzung) as TextView
            detailTextViewVorzeichen = itemView.findViewById<View>(R.id.buch_list_vorzeichen) as TextView
            thumbnailImageView = itemView.findViewById<View>(R.id.buch_list_thumbnail) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitelInBuchViewHolder {

//        Log.v(TAG, "TitelInBuchViewHolder onCreateViewHolder");
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_titel_in_buch, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return TitelInBuchViewHolder(view)
    }

    override fun onBindViewHolder(holder: TitelInBuchViewHolder, position: Int) {

//        Log.v(TAG, "TitelInBuchViewHolder onBindViewHolder");
        when (mList[position].detailListItemType) {
            TitelInBuchClass.Companion.egLiedFundstellen -> {
                /* Some entries in FundstellenLied may also refer to another Lied as same melody */
                /* example "Ach bleib bei uns Herr Jesu Christ" same as "Erhalt uns, Herr, bei Deinem Wort" */
                /* therefore additional entry TitleEGLiedTextView */
                // Additional line just visible for FundstellenLiedFragment
                // https://www.quora.com/How-can-I-set-and-change-the-visibility-to-Linear-Layout-in-Android
                holder.titleEGLiedTextView.visibility = View.VISIBLE
                // Get EGLiedTitel
                holder.titleEGLiedTextView.text = mList[position].titel
                // Get title or buchTitle element
                holder.titleMainTextView.text = mList[position].buchTitel
                // Get titelZusatz or buchUntertitel element
                holder.titleSub1TextView.text = mList[position].zus
                // Get titelKomponist or title element
                holder.titleSub2TextView.text = mList[position].titelKomponist
            }
            TitelInBuchClass.Companion.buchFundstellen -> {
                holder.titleMainTextView.text = mList[position].titel
                holder.titleSub1TextView.text = mList[position].zus
                holder.titleSub2TextView.text = mList[position].titelKomponist
                if (mList[position].audioURL !== "") {
                    holder.playView.visibility = View.VISIBLE
                }
            }
            TitelInBuchClass.Companion.komponistFundstellen -> {
                holder.titleMainTextView.text = mList[position].buchTitel
                holder.titleSub1TextView.text = mList[position].buchUntertitel
                holder.titleSub2TextView.text = mList[position].titel
            }
            TitelInBuchClass.Companion.titelFundstellen -> {
                holder.titleMainTextView.text = mList[position].buchTitel
                holder.titleSub1TextView.text = mList[position].buchUntertitel
                holder.titleSub2TextView.text = mList[position].titelKomponist
            }
        }

        // Get titelNr
        holder.detailTextViewNr.text = mList[position].titelNr
        // Get titelBesetzung
        holder.detailTextViewBesetzung.text = mList[position].titelBesetzung
        // Get titelVorzeichen
        holder.detailTextViewVorzeichen.text = mList[position].titelVorzeichen

        // 3 Making use of the open-source Picasso library for asynchronous image loading
        // -- it helps you download the thumbnail images on a separate thread instead of
        // the main thread. You're also assigning a temporary placeholder for the ImageView to handle slow loading of images.
        // Get thumbnail element 40*60px
        Picasso.get().load(mList[position].imgUrl).placeholder(R.drawable.keinbild).into(holder.thumbnailImageView)
    }

    //1
    override fun getItemCount(): Int {
//        Log.v(TAG, "TitelInBuchViewHolder getItemCount="+mList.size());
        return mList.size
    }

    override fun getFilter(): Filter {
        Log.d(ContentValues.TAG, "TitelInBuchAdapter (getFilter)")
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                Log.d(ContentValues.TAG, "TitelInBuchAdapter (getFilter): $charSequence")
                var charString = charSequence.toString()
                charString = charString.toLowerCase(Locale.getDefault())
                mList = if (charString.isEmpty()) {
                    mList
                } else {
                    val filteredList: MutableList<TitelInBuchClass> = ArrayList()
                    for (wp in mList) {
                        if (wp.buchTitel.toLowerCase(Locale.getDefault()).contains(charString)
                                or wp.titel.toLowerCase(Locale.getDefault()).contains(charString)
                                or wp.titelKomponist.toLowerCase(Locale.getDefault()).contains(charString)) {
                            filteredList.add(wp)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                notifyDataSetChanged()
            }
        }
    }

    init {
        mList = mList
    }
}