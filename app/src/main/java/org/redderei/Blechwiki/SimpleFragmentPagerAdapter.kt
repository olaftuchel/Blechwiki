package org.redderei.Blechwiki

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


/**
 * Created by ot775x on 14.03.2018.
 */
class SimpleFragmentPagerAdapter(private val mContext: Context, fm: FragmentManager?) : FragmentStatePagerAdapter(fm!!) {
    // This determines the fragment for each tab
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            Log.d(ContentValues.TAG, "SimpleFragmentPagerAdapter: LiedFragment selected")
            LiedFragment()
        } else if (position == 1) {
            Log.d(ContentValues.TAG, "SimpleFragmentPagerAdapter: BuchFragment selected")
            BuchFragment()
        } else {
/*
       Log.d(ContentValues.TAG, "SimpleFragmentPagerAdapter: TitelFragment selected")
            TitelFragment()
        } else {
*/
            Log.d(ContentValues.TAG, "SimpleFragmentPagerAdapter: other selected")
            KomponistFragment()
        }
    }

    // This determines the number of tabs
    override fun getCount(): Int {
        return 4
    }

    // This determines the title for each tab
    override fun getPageTitle(position: Int): CharSequence? {
        // Generate title based on item position
        return when (position) {
            0 -> "Lieder"
            1 -> "Bücher"
            2 -> "Komponist"
            3 -> "Titel"
            else -> null
        }
    }
}