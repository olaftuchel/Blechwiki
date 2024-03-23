package org.redderei.Blechwiki

import android.app.Application
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.repository.BlechViewModel
import org.redderei.Blechwiki.util.SharedPreference
import androidx.lifecycle.*


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
        Log.d(ContentValues.TAG, "Mainactivity (define MyViewModelFactory as ViewModelProvider)")
        ViewModelProvider(this, factory).get(BlechViewModel::class.java)
    }
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ContentValues.TAG, "Mainactivity (onCreate): savedInstanceState= $savedInstanceState")

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

        // Greet the user, or ask for their name if new
//        displayWelcome();
    }

    companion object  {
        // runs multiple times, but should not
        lateinit var appContext: MainActivity private set
    }

    private fun displayWelcome() {

        // Read the user's name,
        // or an empty string if nothing found
        val sharedPreference : SharedPreference =SharedPreference(this)
        val name: String = sharedPreference.getValueString(Constant.PREF_NAME).toString()

//        if (name.length() > 0) {
//            // Check if restart or just switching of windows
//            String running = mSharedPreferences.getString(Constant.PREF_RUNNING, "");
//            if (running == "stopped") {
//                // If the name is valid, display a Toast welcoming them
//                Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
//            }
//        } else {
        // otherwise, show a dialog to ask for their name
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Hello!")
        alert.setMessage("What is your name?")

        // Create EditText for entry
        val input = EditText(this)
        alert.setView(input)
        // Make an "OK" button to save the name
        alert.setPositiveButton("OK") { dialog, whichButton -> // Grab the EditText's input
            val inputName = input.text.toString()

            // Put it into memory (don't forget to commit!)
            sharedPreference.save(Constant.PREF_NAME, inputName)

            // Welcome the new user
            Toast.makeText(applicationContext, "Welcome, $inputName!",
                    Toast.LENGTH_LONG).show()
        }
        // Make a "Cancel" button
        // that simply dismisses the alert
        alert.setNegativeButton("Cancel") { dialog, whichButton -> }
        alert.show()
        //        }
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
        Log.d(ContentValues.TAG, "Mainactivity (onRestoreInstanceState)")
        //        someVarA = savedInstanceState.getInt("someVarA");
//        someVarB = savedInstanceState.getString("someVarB");
    }

    public override fun onStart() {
        super.onStart()
        Log.d(ContentValues.TAG, "Mainactivity (onStart)")
    }

    public override fun onResume() {
        super.onResume()
        Log.d(ContentValues.TAG, "Mainactivity (onResume)")
    }

    public override fun onPause() {
        super.onPause()
        Log.d(ContentValues.TAG, "Mainactivity (onPause)")
    }

    public override fun onStop() {
        super.onStop()
        Log.d(ContentValues.TAG, "Mainactivity (onStop)")
    }

    public override fun onDestroy() {
        super.onDestroy()
        Log.d(ContentValues.TAG, "Mainactivity (onDestroy)")
    }
}

class MyViewModelFactory(private val app: Application) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d(ContentValues.TAG, "Mainactivity (MyViewModelFactory)")
        if (modelClass.isAssignableFrom(BlechViewModel::class.java)) {
            return BlechViewModel(app) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}