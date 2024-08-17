
package org.redderei.Blechwiki.util

import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import org.redderei.Blechwiki.*
import org.redderei.Blechwiki.adapter.KomponistAdapter
import org.redderei.Blechwiki.adapter.LiedAdapter
import org.redderei.Blechwiki.adapter.TitelAdapter
import org.redderei.Blechwiki.adapter.TitelInBuchAdapter
import org.redderei.Blechwiki.adapter.BuchAdapter
import java.util.*

object SideIndex {
    // -------- Alphabetischer Index neben der Liste --------
    // http://androidopentutorials.com/android-listview-with-alphabetical-side-index/
    private var indexChar: String? = null

    fun getLiedIndexList(mAdapter: LiedAdapter, sortType: String): Map<String, Int> {
//        Log.v("SideIndex", "getLiedIndexList: sortType=" + sortType);
        val mapIndex: MutableMap<String, Int> = LinkedHashMap<String, Int>()
        var i: Int = 0
        if (mAdapter!!.mList.isNotEmpty()) {
            when (sortType) {
                "ABC" -> {
                    while (i < mAdapter.mList.size) {
                        val indexChar = mAdapter.mList[i].lied.substring(0, 1).uppercase()
                        if (indexChar.matches(Regex("[A-Z]")) && mapIndex[indexChar] == null) mapIndex[indexChar!!] = i
                        i++
                    }
                }
                "Nr" -> {
                    while (i < mAdapter.mList.size) {
                        indexChar = mAdapter.mList[i].nr
                        val ind = indexChar!!.toInt()
                        if (ind % 30 == 0) { mapIndex[indexChar!!] = i
                        }
                        i++
                    }
                }
                "Anlass" -> {
                    while (i < mAdapter.mList.size) {
                        val indexChar = mAdapter.mList[i].anlass.substring(0, 1).uppercase()
                        if (indexChar.matches(Regex("[A-Z]")) && mapIndex[indexChar] == null) mapIndex[indexChar!!] = i
                        i++
                    }
                }

                else -> {
                    Log.d("SideIndex", "getLiedIndexList, ERROR: sortType not found")
                }
            }
        }
        Log.v("SideIndex", "getLiedIndexList: $mapIndex")
        return mapIndex
    }

    fun getBuchIndexList(mAdapter: BuchAdapter): Map<String, Int> {
        // calculate Index List and show it up
        val mapIndex: MutableMap<String, Int> = LinkedHashMap<String, Int>()
        if (mAdapter.mList.isNotEmpty()) {
            for (i: Int in mAdapter.mList.indices) {
                indexChar = mAdapter.mList[i].buch.substring(0, 1).uppercase()
                if (indexChar!!.matches(Regex("[A-Z]")) && mapIndex[indexChar] == null) mapIndex[indexChar!!] = i
            }
        }
        Log.v("SideIndex", "getBuchIndexList: $mapIndex")
        return mapIndex
    }

    fun getKomponistIndexList(mAdapter: KomponistAdapter): Map<String, Int> {
        val mapIndex: MutableMap<String, Int> = LinkedHashMap<String, Int>()
        if (mAdapter.mList.isNotEmpty()) {
            for (i: Int in mAdapter.mList.indices) {
                indexChar = mAdapter.mList[i].komponist.substring(0, 1).uppercase()
                if (indexChar!!.matches(Regex("[A-Z]")) && mapIndex[indexChar] == null) mapIndex[indexChar!!] = i
            }
        }
        Log.v("SideIndex", "getKomponistIndexList: $mapIndex")
        return mapIndex
    }

    fun getTitelIndexList(mAdapter: TitelAdapter): Map<String, Int> {
        val mapIndex: MutableMap<String, Int> = LinkedHashMap<String, Int>()
        if (mAdapter.mList.isNotEmpty()) {
            for (i: Int in mAdapter.mList.indices) {
                indexChar = mAdapter.mList[i].titel.substring(0, 1).uppercase()
                if (indexChar!!.matches(Regex("[A-Z]")) && mapIndex[indexChar] == null) mapIndex[indexChar!!] = i
            }
        }
        Log.v("SideIndex", "getTitelIndexList: $mapIndex")
        return mapIndex
    }

    fun getFundstellenBuchIndexList(mAdapter: TitelInBuchAdapter, sortType: String): Map<String, Int> {
        // calculate Index List and show it up
        val mapIndex: MutableMap<String, Int> = LinkedHashMap<String, Int>()
        if (mAdapter!!.mList.isNotEmpty()) {
            if (sortType == "Nr") {    // called from FundstellenBuchFragment
                for (i: Int in mAdapter.mList.indices) {
                    indexChar = mAdapter.mList[i].nr
                    var ind = indexChar
                    if (ind!!.toInt() % 10 == 0) {
                        mapIndex[indexChar!!] = i
                    }
                }
            } else {    // called from FundstellenLiedFragment
                for (i: Int in mAdapter.mList.indices) {
                    indexChar = mAdapter.mList[i].buch.substring(0, 1).uppercase()
                    if (indexChar!!.matches(Regex("[A-Z]")) && mapIndex[indexChar] == null) mapIndex[indexChar!!] = i
                }
            }
        }
        Log.v("SideIndex", "getFundstellenBuchIndexList: $mapIndex")
        return mapIndex
    }

    fun displayIndex(mapIndex: Map<String, Int>?, rootView: View?, mGetLayoutInflater: LayoutInflater, mOnClickListener: View.OnClickListener?) {
        Log.v("SideIndex", "displayindex")
        val indexLayout = rootView!!.findViewById<LinearLayout>(R.id.side_index2)
        indexLayout.removeAllViews() // remove any possible old content
        var textView: TextView
        val indexList: List<String> = ArrayList<String>(mapIndex!!.keys)
        for (indexChar in indexList) {
            textView = mGetLayoutInflater.inflate(R.layout.side_index_item, null) as TextView
            textView.text = indexChar
            textView.setOnClickListener(mOnClickListener)
            indexLayout.addView(textView)
        }
    }
}



