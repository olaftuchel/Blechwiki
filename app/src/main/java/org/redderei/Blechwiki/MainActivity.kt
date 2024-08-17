package org.redderei.Blechwiki

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.util.SharedPreference

/**Features
 * Tabview (Lieder, Bücher, Komponist, Titel)
 * Scrollview (main page) with index letters right
 * filter main item (index letters change)
 * Details view
 * Filter per Kirche
 * Sortierung Lieder (ABC, Nummer, Thema(Abend, Abendmahl, Advent))
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d( "Mainactivity", "onCreate: savedInstanceState= $savedInstanceState")

        // store context for later use
        appContext = this

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main)

        // Find the view pager that will allow the user to swipe between fragments
        val viewPager = findViewById<View>(R.id.viewpager) as ViewPager

        // Create an adapter that knows which fragment should be shown on each page
        val adapter = SimpleFragmentPagerAdapter(applicationContext, supportFragmentManager)

        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        // Give the TabLayout the ViewPager
        val tabLayout = findViewById<View>(R.id.sliding_tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        val sharedPreference: SharedPreference = SharedPreference(appContext)
        // init values on first run
        if (!sharedPreference.getValueBoolean(Constant.PREF_INITIALIZED, false)) {
            initSharedPreferences(sharedPreference)
        }
    }

    companion object {
        // runs multiple times, but should not
        lateinit var appContext: MainActivity private set
    }

    fun initSharedPreferences(sharedPreference: SharedPreference) {
        Log.v("MainActivity", "Init shared preference values as 0")
        // Values not initialized so far
        // changecounter = 0: pull the whole set of data
        // changecounter > 0: pull the update from changecounter to last package set
        sharedPreference.save(Constant.PREF_SORTTYPE, "ABC")
        sharedPreference.save(Constant.PREF_KIRCHE, "N")
        sharedPreference.save(Constant.PREF_CHANGECOUNTER_LIED, 0)  // we do not expect changes
        sharedPreference.save(Constant.PREF_CHANGECOUNTER_BUCH, 0)
        sharedPreference.save(Constant.PREF_CHANGECOUNTER_KOMPONIST, 0)
        sharedPreference.save(Constant.PREF_CHANGECOUNTER_TITEL, 0)
        sharedPreference.save(Constant.PREF_INITIALIZED, true)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState);
        Log.d( "Mainactivity", "onRestoreInstanceState")
        //        someVarA = savedInstanceState.getInt("someVarA");
//        someVarB = savedInstanceState.getString("someVarB");
    }
}
