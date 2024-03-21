package org.redderei.Blechwiki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by ot775x on 23.05.2018.
 */
/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [LiedFragment].
 *
 *
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a [FundstellenTitelFragment].
 */
class ActivityDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /*
        Log.v(ContentValues.TAG, "ActivityDetail: onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_container)

        // https://stackoverflow.com/questions/15686555/display-back-button-on-action-bar
        // this disables back button; you may set it to home as well
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val arguments = Bundle()
            val callingFragment = intent.getStringExtra("CALLING_FRAGMENT")
            when (callingFragment) {
                "LiedFragment" -> {
                    val ixUr = intent.getStringExtra(FundstellenLiedFragment.Companion.ARG_ITEM_IXUR)
                    val liedTitel = intent.getStringExtra(FundstellenLiedFragment.Companion.ARG_ITEM_LIED)
                    arguments.putString(FundstellenLiedFragment.Companion.ARG_ITEM_IXUR, ixUr)
                    arguments.putString(FundstellenLiedFragment.Companion.ARG_ITEM_LIED, liedTitel)
                    val lFragment = FundstellenLiedFragment()
                    lFragment.arguments = arguments
                    supportFragmentManager.beginTransaction().add(R.id.activity_detail_container, lFragment).commit()
                }
                "BuchFragment" -> {
                    val buchId = intent.getStringExtra(FundstellenTitelFragment.Companion.ARG_ITEM_BUCH)
                    val buch = intent.getStringExtra(FundstellenTitelFragment.Companion.ARG_ITEM_LONGNAME)
                    arguments.putString(FundstellenTitelFragment.Companion.ARG_ITEM_BUCH, buchId)
                    arguments.putString(FundstellenTitelFragment.Companion.ARG_ITEM_LONGNAME, buch)
                    val bFragment = FundstellenTitelFragment()
                    bFragment.arguments = arguments
                    supportFragmentManager.beginTransaction().add(R.id.activity_detail_container, bFragment).commit()
                }
                "KomponistFragment" -> {
                    val shortKomponist = intent.getStringExtra(FundstellenTitelFragment.Companion.ARG_ITEM_KOMPONIST)
                    val komponist = intent.getStringExtra(FundstellenTitelFragment.Companion.ARG_ITEM_LONGNAME)
                    arguments.putString(FundstellenTitelFragment.Companion.ARG_ITEM_KOMPONIST, shortKomponist)
                    arguments.putString(FundstellenTitelFragment.Companion.ARG_ITEM_LONGNAME, komponist)
                    val kFragment = FundstellenTitelFragment()
                    kFragment.arguments = arguments
                    supportFragmentManager.beginTransaction().add(R.id.activity_detail_container, kFragment).commit()
                }
                "TitelFragment" -> {
                    val titel = intent.getStringExtra(FundstellenBuchFragment.Companion.ARG_ITEM_TITEL)
                    val titelIx = intent.getStringExtra(FundstellenBuchFragment.Companion.ARG_ITEM_TITEL_IX)
                    arguments.putString(FundstellenBuchFragment.Companion.ARG_ITEM_TITEL_IX, titelIx)
                    arguments.putString(FundstellenBuchFragment.Companion.ARG_ITEM_TITEL, titel)
                    val tFragment = FundstellenBuchFragment()
                    tFragment.arguments = arguments
                    supportFragmentManager.beginTransaction().add(R.id.activity_detail_container, tFragment).commit()
                }
            }
        }

         */
    }
    /**
     * how do we activate options here?
     * @Override
     * public boolean onOptionsItemSelected (MenuItem item) {
     * Log.v(TAG, "LiedDetailActivity: onOptionsItemSelected");
     * int id = item.getItemId();
     * if (id == android.R.id.home) {
     * // This ID represents the Home or Up button. In the case of this
     * // activity, the Up button is shown. Use NavUtils to allow users
     * // to navigate up one level in the application structure. For
     * // more details, see the Navigation pattern on Android Design:
     * //
     * // http://developer.android.com/design/patterns/navigation.html#up-vs-back
     * //
     * NavUtils.navigateUpTo(this, new Intent(this, LiedFragment.class));
     * return true;
     * }
     * return super.onOptionsItemSelected(item);
     * }
     */
}