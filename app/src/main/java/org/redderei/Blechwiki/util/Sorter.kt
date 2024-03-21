package org.redderei.Blechwiki.util

import android.content.ContentValues
import android.util.Log
import org.redderei.Blechwiki.adapter.KomponistAdapter
import org.redderei.Blechwiki.adapter.TitelAdapter
import org.redderei.Blechwiki.adapter.TitelInBuchAdapter
import org.redderei.Blechwiki.gettersetter.KomponistClass
import org.redderei.Blechwiki.gettersetter.TitelClass
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import java.text.Collator
import java.util.*

open class Sorter internal constructor(order: Int) : Comparator<Any> {
    var order = -1

    // dummy installation
    override fun compare(o1: Any, o2: Any): Int {
        return -1
    }

    companion object {
        fun sortKomponist(adapter: KomponistAdapter, order: Int) {
            Log.v(ContentValues.TAG, "Sorter.sortKomponist")
            Collections.sort(adapter.mKomponistList, object : Sorter(order) {
                var germanCollator = Collator.getInstance(Locale.GERMANY)
                override fun compare(ob1: Any, ob2: Any): Int {
                    val komponist1 = (ob1 as KomponistClass).Komponist
                    val komponist2 = (ob2 as KomponistClass).Komponist
                    return germanCollator.compare(komponist1, komponist2)
                }
            })
            adapter.notifyDataSetChanged()
        }

        // not in use
        fun sortTitel(adapter: TitelAdapter, order: Int) {
            Log.v(ContentValues.TAG, "Sorter.sortTitel")
            Collections.sort(adapter.mTitelList, object : Sorter(order) {
                var germanCollator = Collator.getInstance(Locale.GERMANY)
                override fun compare(ob1: Any, ob2: Any): Int {
                    val titel1 = (ob1 as TitelClass).Titel
                    val titel2 = (ob2 as TitelClass).Titel
                    return germanCollator.compare(titel1, titel2)
                }
            })
            adapter.notifyDataSetChanged()
        }

        fun sortFundstellenLied(adapter: TitelInBuchAdapter?, order: Int) {
            Log.v(ContentValues.TAG, "Sorter.sortFundstellenLied")
            Collections.sort(adapter!!.mList, object : Sorter(order) {
                var germanCollator = Collator.getInstance(Locale.GERMANY)
                override fun compare(ob1: Any, ob2: Any): Int {
                    val buch1 = (ob1 as TitelInBuchClass).buchTitel
                    val buch2 = (ob2 as TitelInBuchClass).buchTitel
                    return germanCollator.compare(buch1, buch2)
                }
            })
            adapter.notifyDataSetChanged()
        }

        fun sortFundstellenBuch(adapter: TitelInBuchAdapter?, order: Int) {
            Log.v(ContentValues.TAG, "Sorter.sortFundstellenBuch")
            Collections.sort(adapter!!.mList, object : Sorter(order) {
                var germanCollator = Collator.getInstance(Locale.GERMANY)
                override fun compare(ob1: Any, ob2: Any): Int {
                    val buch1 = (ob1 as TitelInBuchClass).buchTitel
                    val buch2 = (ob2 as TitelInBuchClass).buchTitel
                    return germanCollator.compare(buch1, buch2)
                }
            })
            adapter.notifyDataSetChanged()
        }
    }

    init {
        this.order = order
    }
}