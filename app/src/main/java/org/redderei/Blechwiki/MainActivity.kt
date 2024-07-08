package org.redderei.Blechwiki

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.StoreVars
import org.redderei.Blechwiki.repository.AutoNrViewModel
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
/*    public val blechViewModel: BlechViewModel by lazy {
        val factory = MyViewModelFactory(app = Application())
        Log.d( "Mainactivity", "define MyViewModelFactory as ViewModelProvider)")
        ViewModelProvider(this, factory).get(BlechViewModel::class.java)
    }
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d( "Mainactivity", "onCreate: savedInstanceState= $savedInstanceState")

        // store context for later use
        appContext = this
        val autoNrViewModel = ViewModelProvider(appContext).get(AutoNrViewModel::class.java)
        autoNrViewModel.getAutoNr
        Log.d("MainActivity", "init: StoreVars, autoNrBuch=${StoreVars.instance.autoNrBuch} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")

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

        // Greet the user, or ask for their name if new
//        displayWelcome();
    }

    companion object  {
        // runs multiple times, but should not
        lateinit var appContext: MainActivity private set
    }

    //    @Override
    //    protected void onSaveInstanceState(Bundle outState) {
    //        super.onSaveInstanceState(outState);
    //        Log.d(TAG, "Mainactivity (onSaveInstanceState)");
    //        outState.putInt("someVarA", someVarA);
    //        outState.putString("someVarB", someVarB);
    //    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState);
        Log.d( "Mainactivity", "onRestoreInstanceState")
        //        someVarA = savedInstanceState.getInt("someVarA");
//        someVarB = savedInstanceState.getString("someVarB");
    }
}
