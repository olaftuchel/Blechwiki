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

class BlechRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    private val i = 0
    val sharedPreference: SharedPreference = SharedPreference(appContext)

/*
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    fun getAllLieder(mKirche: String, sortType: String, query: String): LiveData<List<LiedClass>>? {

        Log.d(
            ContentValues.TAG,
            "BlechRepository (getAllLieder) mKirche=$mKirche sortType=$sortType"
        )
//        refreshLieder()
        Log.d(ContentValues.TAG, "BlechRepository (getAllLieder) sortType=$sortType ")
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
*/
    /** insert done in ParseResponse now
     * // You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * // that you're not doing any long running operations on the main thread, blocking the UI.
     * void insertLied(LiedClass lied) {
     * i = Integer.parseInt(lied.getIx());
     * if (i%100 == 0 )
     * Log.v(TAG, "BlechRepository (insertLied) " + lied.getNr() + " " + lied.getLied() + " " + lied.getTeil());
     * BlechDatabase.databaseWriteExecutor.execute(() -> {
     * mBlechDao.insert(lied);
     * });
     * }
     */
/*
    private fun refreshLieder() {      // fetch fresh data if nothing there yet
        if (sharedPreference.getValueString(Constant.PREF_AUTO_NR_LIED) == 0) {
            Log.d(ContentValues.TAG, "BlechRepository (refreshLieder): refresh data from SOAP server")
            val clickAction = "GetEGLieder2"
            val searchString = ""
            // define soapTask
            soapTask = SoapTask(context)
            // define SoapTask.DownloadCompleteListenerLied, once defined it won't be called again
            soapTask!!.setDownloadCompleteListener(object : SoapTask.DownloadCompleteListener {
                override fun onUpdate(mList: List<*>?) {
                    if (soapTask!!.isCancelled) {
                        Toast.makeText(context, "BlechRepository(refreshLieder): Network Error", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            // execute soapTask
            soapTask!!.execute(clickAction, searchString, mBlechDao, mList)
            Log.d(ContentValues.TAG, "refreshLieder: ..... changed mList to List<TypeVariable> mList, might fail!!!")
        }
    }
*/

    suspend fun getAllBuch(query: String): LiveData<List<BuchClass>>? {
        Log.d(TAG, "BlechRepository (getAllBuch): sharedPreference AutoNrBuch=" +
                "${sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH)}, StoreVars.autoNrBuch=${StoreVars.instance.autoNrBuch}")
        if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH) < StoreVars.instance.autoNrBuch) {
            Log.d(TAG, "BlechRepository (getAllBuch): refresh data from REST server")
            val restBlechwiki = ServiceBuilder.buildService(GetRestBlechwiki::class.java)
            val call = restBlechwiki.search("Buch&counter=0")
            call.enqueue(object : Callback<List<BuchClass>> {
                override fun onResponse(
                    call: Call<List<BuchClass>>,
                    restResponse: Response<List<BuchClass>>
                ) {
                    Log.d(TAG, "BlechRepository (getAllBuch) onResponse: we got ${restResponse.body()}")
                    if (restResponse.isSuccessful) {
                        Log.d(TAG, "Response: Buch size : ${restResponse.body()?.size}")
                        //                       ParseResponse.buch(restResponse, clickAction)
//                        restResponse.body()?.forEach{insertBuch(it)}
//                        val tableListinsert: List<BuchClass> = restResponse.filter{it.change == "new" }    // .filter{it.change == "new"}
                        val tableListinsert: List<BuchClass>? = restResponse.body()
                        GlobalScope.launch { tableListinsert?.forEach { insertBuch(it) } }
                        sharedPreference.save(Constant.PREF_AUTO_NR_BUCH, StoreVars.instance.autoNrBuch)
                    } else {
                        Log.d(
                            TAG,
                            "BlechRepository (getAllBuch) onResponse: no success in retrieving data, ${restResponse.message()}"
                        )
                        Toast.makeText(
                            appContext, "BlechRepository(getAllBuch)onResponse: no success in retrieving data, ${restResponse.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<BuchClass>>, t: Throwable) {
                    Log.d("BlechRepository (getAllBuch): onFailure", "Something went wrong $t")
                    Toast.makeText(
                        appContext,
                        "BlechRepository (getAllBuch): Error $t",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
        Log.d(TAG, "BlechRepository: retrieve Bücher")
        return mBlechDao.getAllBuecher(query)
    }

    suspend fun insertBuch(buch: BuchClass) {
        Log.v(TAG, "BlechRepository (insertBuch) " + buch.Buch);
        mBlechDao.insert(buch);
    }

    fun getChangeBuch(changeCounter: Int) {
        Log.d(TAG, "BlechRepository (getChangeBuch)")
        val restBlechwiki = ServiceBuilder.buildService(GetRestBlechwiki::class.java)
        val call = restBlechwiki.search("RestBlechWiki/api/Buch&counter=${changeCounter}")
        call.enqueue(object : Callback<List<BuchClass>> {
            override fun onResponse(
                call: Call<List<BuchClass>>,
                restResponse: Response<List<BuchClass>>
            ) {
                Log.d(TAG, "getChangeBuch onResponse: we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d(TAG, "Response: Buch size : ${restResponse.body()?.size}")
                    //                       ParseResponse.buch(restResponse, clickAction)
//                        restResponse.body()?.forEach{insertBuch(it)}
//                        val tableListinsert: List<BuchClass> = restResponse.filter{it.change == "new" }    // .filter{it.change == "new"}
                    val tableListinsert: List<BuchClass>? = restResponse.body()
                    GlobalScope.launch { tableListinsert?.forEach { insertBuch(it) } }
                    sharedPreference.save(Constant.PREF_AUTO_NR_BUCH, StoreVars.instance.autoNrBuch)
                } else {
                    Log.d(
                        TAG,
                        "getGetAutoNr: onResponse: Something went wrong ${restResponse.message()}"
                    )
                    Toast.makeText(
                        appContext, "BlechRepository(getGetAutoNr): ${restResponse.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<BuchClass>>, t: Throwable) {
                Log.d("getBuchList: onFailure", "Something went wrong $t")
                Toast.makeText(
                    appContext,
                    "BlechRepository(getBuchList): Error $t",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    fun getAllKomponist(query: String?): LiveData<List<KomponistClass>>? {
        Log.d(ContentValues.TAG, "BlechRepository (getAllKomponist)")
        // checks for latest data, in case of need calls soapserver, deletes database entries and inserts again
        refreshKomponist()
        return mBlechDao.getAllKomponist(query)
    }

    fun deleteAllKomponist() {
        mBlechDao.deleteAllKomponist()
    }

    fun refreshKomponist() {      // fetch fresh data if nothing there yet
        if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_KOMPONIST) == 0) {
            Log.d(ContentValues.TAG, "BlechRepository (refreshKomponist): refresh data from server")
            Toast.makeText(
                appContext,
                "Komponisten werden gelesen, bitte einen Moment warten",
                Toast.LENGTH_SHORT
            ).show()
            val clickAction = "GetKomponisten"
            val searchString = "0"

            val destinationService = ServiceBuilder.buildService(RestGetKomponistList::class.java)
            val requestCall = destinationService.getKomponistList(searchString)
            //make network call asynchronously
            requestCall.enqueue(object : Callback<List<KomponistClass>> {
                override fun onResponse(
                    call: Call<List<KomponistClass>>,
                    response: Response<List<KomponistClass>>
                ) {
                    Log.d("Response", "onResponse: ${response.body()}")
                    if (response.isSuccessful) {
                        val KomponistenString = response.body()!!
                        Log.d("Response", "Komponist size : ${KomponistenString.size}")
                        for (i in 1..5) {
                            val komp = KomponistenString.get(i).FriendlyKomponistName
                            println("KomponistClass.get($i)=$komp")
                        }
                    } else {
                        Log.d(
                            "refreshKomponist: onResponse",
                            "Something went wrong ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<List<KomponistClass>>, t: Throwable) {
                    Log.d("refreshKomponist: onFailure", "Something went wrong $t")
                }
            })
            Log.d(
                ContentValues.TAG,
                "refreshKomponist: ..... changed mList to List<TypeVariable> mList, might fail!!!"
            )
        }
    }

    fun getAllTitel(query: String?): LiveData<List<TitelClass>>? {
/*
        Log.d(ContentValues.TAG, "BlechRepository (getAllTitel)")
        refreshTitel()
        return mBlechDao.getAllTitel(query)
*/
        return null
    }

    fun getAllTitelKomma(query: String?): LiveData<List<TitelClass>>? {
        Log.d(ContentValues.TAG, "BlechRepository (getAllTitelKomma)")
//        refreshTitel()
//        return mBlechDao.getAllTitelKomma(query)
        return null
    }

/*
    fun refreshTitel() {      // fetch fresh data if nothing there yet
        if (sharedPreference.getValueString(Constant.PREF_AUTO_NR_TITEL) == 0) {
            Log.d(ContentValues.TAG, "BlechRepository (refreshTitel): refresh data from SOAP server")
            val clickAction = "GetTitel2"
            val searchString = ""
            // define soapTask
            soapTask = SoapTask(context)
            // define SoapTask.DownloadCompleteListenerLied, once defined it won't be called again
            soapTask!!.setDownloadCompleteListener(object : SoapTask.DownloadCompleteListener {
                override fun onUpdate(mList: List<*>?) {
                    if (soapTask!!.isCancelled) {
                        Toast.makeText(context, "BlechRepository(refreshTitel): Network Error", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            // execute soapTask
            soapTask!!.execute(clickAction, searchString, mBlechDao, mList)
            Log.d(ContentValues.TAG, "refreshTitel: ..... changed mList to List<TypeVariable> mList, might fail!!!")
        }
    }//in case of different values reset it -> data are dropped and pulled again
*/
    /**
     * 1. retrieves latest update number of Buch/Komponist/Titel from REST
     * 2. compares value to existing value in shared preferences
     *      values:
     *          first Start after Installation or didn't retrieve values yet:   sharedPref = 0,     Store = -1
     *          another Start without change                                    sharedPref = lastNr Store = lastNr
     *          another Start with change                                       sharedPref = lastNr < Store
     * 3. sets shared preferences value to 0 if latest getGetAutoNr is different
     *      a.  getMaxChangecounterBuch, getMaxChangecounterKomponist, getMaxChangecounterTitel
     *      b.  retrieve from REST is done by getChangeBuch
     * AutoNr1 ~ Buch; AutoNr2 ~ Komponist; AutoNr3 ~ Titel
     */
    // app so far without local data storage
    // Values not initialized so far
/*
    val getAutoNr: Unit
    /* different values of sharedPreferences and StoreVars for different statuses:
        directly after installation (or didn't receive any values from Web):
                                        sharedPref = -1        StoreVars = 0
        new start without changes       sharedPref = lastNr    StoreVars = lastNr
        new start with changes in web   sharedPref = lastNr  < StoreVars
     */
        get() {
            if (!sharedPreference.getValueBoolean(Constant.PREF_INITIALIZED, false)) {          // app didn't store preferences so far, initialize them
                Log.v(ContentValues.TAG, "BlechRepository (getGetAutoNr) runs first time on machine")
                // Values not initialized so far
                sharedPreference.save(Constant.PREF_SORTTYPE, "ABC")
                sharedPreference.save(Constant.PREF_KIRCHE, "N")
                sharedPreference.save(Constant.PREF_AUTO_NR_LIED, -1)
                sharedPreference.save(Constant.PREF_AUTO_NR_BUCH, -1)
                sharedPreference.save(Constant.PREF_AUTO_NR_KOMPONIST, -1)
                sharedPreference.save(Constant.PREF_AUTO_NR_TITEL, -1)
            }
            if (StoreVars.instance.autoNrBuch == 0) {                                                           // query AutoNr just directly after program start
                if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH) < StoreVars.instance.autoNrBuch) { // latest run of this program received lower values
                    Log.v(ContentValues.TAG, "BlechRepository (getGetAutoNr) runs again on machine, " +
                            "compare local data serial PrefAutoNrBuch=" + sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH) +
                            " StoreVars.instance.autoNrBuch" + StoreVars.instance.autoNrBuch)
                    val destinationService = ServiceBuilder.buildService(RestGetAutoNr::class.java)             //ApiInterface.RestGetAutoNr interface
                    val call = destinationService.getAutoNr()
                    // make network call asynchronously, read three values latest getGetAutoNr from Web
                    call.enqueue(object : Callback<AutoNrClass> {
                        override fun onResponse(call: Call<AutoNrClass>, response: Response<AutoNrClass>) {
                            Log.d("getGetAutoNr: Response", "onResponse: ${response.body()}")
                            if (response.isSuccessful){
                                sharedPreference.save(Constant.PREF_INITIALIZED, true)
                                val AutoNrString = response.body()!!
                                Log.d(TAG, "BlechRepository.getAutoNr(onResponse) AutoNrString= ${AutoNrString}")
                                StoreVars.instance.autoNrBuch = AutoNrString.AutoNr.get(0).lastNr         // with SOAP was lastNr +1
                                StoreVars.instance.autoNrKomponist = AutoNrString.AutoNr.get(1).lastNr
                                StoreVars.instance.autoNrTitel = AutoNrString.AutoNr.get(2).lastNr
                                Log.d(ContentValues.TAG, "BlechRepository (getGetAutoNr): autoNrBuch=" + StoreVars.instance.autoNrBuch
                                        + " autoNrKomponist=" + StoreVars.instance.autoNrKomponist + " autoNrTitel=" + StoreVars.instance.autoNrTitel)
                                //in case of different values reset it -> data are dropped and pulled again
                                if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH) != StoreVars.instance.autoNrBuch) {
                                    Toast.makeText(appContext, "Bücher haben sich geändert", Toast.LENGTH_SHORT).show()
                                    sharedPreference.save(Constant.PREF_AUTO_NR_BUCH, 0)
                                }
                                if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_KOMPONIST) != StoreVars.instance.autoNrKomponist) {
                                    Toast.makeText(appContext, "Komponisten haben sich geändert", Toast.LENGTH_SHORT).show()
                                    sharedPreference.save(Constant.PREF_AUTO_NR_KOMPONIST, 0)
                                }
                                if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_TITEL) != StoreVars.instance.autoNrTitel) {
                                    Toast.makeText(appContext, "Titel haben sich geändert", Toast.LENGTH_SHORT).show()
                                    sharedPreference.save(Constant.PREF_AUTO_NR_TITEL, 0)
                                }

                            } else {
                                Log.d(TAG, "BlechRepository (getAutoNr onResponse): Something went wrong ${response.message()}")
                                Toast.makeText(appContext, "BlechRepository(getAutoNr): ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<AutoNrClass>, t: Throwable) {
                            Log.d(TAG, "BlechRepository (getAutoNr onFailure): Something went wrong $t")
                            Toast.makeText(appContext, "BlechRepository(getAutoNr): Error $t", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }

    val getLastNr: Unit
        get() {
            Log.v(ContentValues.TAG, "BlechRepository (getLastNr)")
            mBlechDao.getMaxBuchId()
        }
*/
    init {
        // initialize database connection
        Log.d(ContentValues.TAG, "BlechRepository (init)")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!

/*        // initialize store for global variables
        if (StoreVars.instance.autoNrBuch == 0) {
//            val blechViewModel = ViewModelProvider(appContext).get(BlechViewModel::class.java)
//          blechViewModel.getAutoNr    statement above resulted in endless calls of init statement
            getAutoNr
            Log.d(ContentValues.TAG, "BlechRepository (init): autoNrBuch=${StoreVars.instance.autoNrBuch} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
*/
    }
}

//private fun <T> Call<T>.enqueue(callback: Callback<List<BuchClass>>) {
//    Log.d(TAG, "callback enqueue, what happens???????????????????????????????")
//}
