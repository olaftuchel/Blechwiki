package org.redderei.Blechwiki.repository

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.redderei.Blechwiki.gettersetter.LiedClass
import org.redderei.Blechwiki.gettersetter.StoreVars
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.util.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 xxx ViewRepository:
  - ViewModels retrieve all data from here
  - Data are retrieved from Web and feed SQL database
  - LiveData makes changes visible
*/

class LiedRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    val sharedPreference: SharedPreference = SharedPreference(appContext)


    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    suspend fun getAllLieder(mKirche: String, sortType: String, query: String): LiveData<List<LiedClass>>? {

        Log.d(ContentValues.TAG, "LiedRepository (getAllLieder) mKirche=${mKirche} sortType=${sortType} query=${query}" +
            " sharedPreference AutoNrBuch=" + "${sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH)}, StoreVars.autoNrBuch=${StoreVars.instance.autoNrBuch}")
/*
        // no data there yet or part of it missing
        if (!sharedPreference.getValueBoolean(Constant.PREF_INITIALIZED, false)) {
            Log.d(TAG, "LiedRepository (getAllLieder): refresh data from REST server")
            val destinationService = ServiceBuilder.buildService(RestGetLiedList::class.java)
            //ApiInterface.RestGetBuchList interface
            val call = destinationService.getLiedList()
            call.enqueue(object : Callback<List<LiedClass>> {
                override fun onResponse(
                    call: Call<List<LiedClass>>,
                    restResponse: Response<List<LiedClass>>
                ) {
                    Log.d(TAG, "LiedRepository (getAllLieder) onResponse: we got ${restResponse.body()}")
                    if (restResponse.isSuccessful) {
                        Log.d(TAG, "Response: Lieder size : ${restResponse.body()?.size}")
//                        restResponse.body()?.forEach{insertBuch(it)}
//                        val tableListinsert: List<LiedClass> = restResponse.filter{it.change == "new" }    // .filter{it.change == "new"}
                        val tableListinsert: List<LiedClass>? = restResponse.body()
                        GlobalScope.launch { tableListinsert?.forEach { insertLied(it) } }
                        sharedPreference.save(Constant.PREF_AUTO_NR_LIED, StoreVars.instance.autoNrLied)
                    } else {
                        Log.d(TAG, "LiedRepository (getAllLied) onResponse: no success in retrieving data, ${restResponse.message()}")
                        Toast.makeText(
                            appContext, "LiedRepository(getAllLied) onResponse: no success in retrieving data, ${restResponse.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<LiedClass>>, t: Throwable) {
                    Log.d("LiedRepository (getAllLied): onFailure", "Something went wrong $t")
                    Toast.makeText(
                        appContext,
                        "LiedRepository (getAllLied): Error $t",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
*/
//        refreshLieder()

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

    suspend fun insertLied(lied: LiedClass) {
        Log.v(TAG, "LiedRepository (insertLied) " + lied.lied);
        mBlechDao.insert(lied);
    }

/*
    suspend fun refreshLieder() {      // fetch fresh data if nothing there yet
        if (sharedPreference.getValueString(Constant.PREF_AUTO_NR_LIED) == 0) {
            Log.d(ContentValues.TAG, "LiedRepository (refreshLieder): refresh data from SOAP server")
            val clickAction = "GetEGLieder2"
            val searchString = ""
            // define soapTask
            soapTask = SoapTask(context)
            // define SoapTask.DownloadCompleteListenerLied, once defined it won't be called again
            soapTask!!.setDownloadCompleteListener(object : SoapTask.DownloadCompleteListener {
                override fun onUpdate(mList: List<*>?) {
                    if (soapTask!!.isCancelled) {
                        Toast.makeText(context, "LiedRepository(refreshLieder): Network Error", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            // execute soapTask
            soapTask!!.execute(clickAction, searchString, mBlechDao, mList)
            Log.d(ContentValues.TAG, "refreshLieder: ..... changed mList to List<TypeVariable> mList, might fail!!!")
        }
    }
*/

    init {
        // initialize database connection
        Log.d(ContentValues.TAG, "LiedRepository (init)")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!

        // initialize store for global variables
        if (StoreVars.instance.autoNrBuch == 0) {
            val autoNrViewModel = ViewModelProvider(appContext).get(AutoNrViewModel::class.java)
            autoNrViewModel.getAutoNr
            Log.d(ContentValues.TAG, "LiedRepository (init): autoNrBuch=${StoreVars.instance.autoNrBuch} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
    }
}

//private fun <T> Call<T>.enqueue(callback: Callback<List<LiedClass>>) {
//    Log.d(TAG, "callback enqueue, what happens???????????????????????????????")
//}
