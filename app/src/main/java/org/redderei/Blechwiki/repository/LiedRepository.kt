package org.redderei.Blechwiki.repository

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import org.redderei.Blechwiki.gettersetter.AutoNrClass
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.LiedClass
import org.redderei.Blechwiki.gettersetter.BuchClass
import org.redderei.Blechwiki.gettersetter.KomponistClass
import org.redderei.Blechwiki.gettersetter.TitelClass
import org.redderei.Blechwiki.gettersetter.StoreVars
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.util.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiedRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    private val i = 0
    val sharedPreference: SharedPreference = SharedPreference(appContext)


    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    fun getAllLieder(mKirche: String, sortType: String, query: String): LiveData<List<LiedClass>>? {

        Log.d(
            ContentValues.TAG,
            "LiedRepository (getAllLieder) mKirche=$mKirche sortType=$sortType"
        )
//        refreshLieder()
        Log.d(ContentValues.TAG, "LiedRepository (getAllLieder) sortType=$sortType ")
        return when (sortType) {
            "ABC" -> {
                mBlechDao.getAllLiederSortABC(mKirche, query)
            }
            "Nr" -> {
                mBlechDao.getAllLiederSortNr(mKirche, query)
            }
            "Anlass" -> {
                mBlechDao.getAllLiederSortAnlass(mKirche, query)
            }
            else -> null
        }
    }

    init {
        // initialize database connection
        Log.d(ContentValues.TAG, "LiedRepository (init)")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!
/*
        // initialize store for global variables
        if (StoreVars.instance.autoNrBuch == 0) {
//            val blechViewModel = ViewModelProvider(appContext).get(BlechViewModel::class.java)
//          blechViewModel.getAutoNr    statement above resulted in endless calls of init statement
            getAutoNr
            Log.d(ContentValues.TAG, "LiedRepository (init): autoNrBuch=${StoreVars.instance.autoNrBuch} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
 */
    }
}

//private fun <T> Call<T>.enqueue(callback: Callback<List<BuchClass>>) {
//    Log.d(TAG, "callback enqueue, what happens???????????????????????????????")
//}
