package org.redderei.Blechwiki.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.redderei.Blechwiki.gettersetter.KomponistClass
import org.redderei.Blechwiki.gettersetter.StoreVars
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
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

class KomponistRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    val sharedPreference: SharedPreference = SharedPreference(appContext)

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    suspend fun getAllKomponist(query: String): LiveData<List<KomponistClass>>? {

        Log.d("KomponistRepository", "getAllKomponist: query=>${query}< sharedPreference AutoNrKomponist="
                + "${sharedPreference.getValueInt(Constant.PREF_AUTO_NR_KOMPONIST)}, StoreVars.autoNrKomponist=${StoreVars.instance.autoNrKomponist}")

        // no data there yet or part of it missing
        if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_KOMPONIST) < StoreVars.instance.autoNrKomponist) {
            Log.d("KomponistRepository", "getAllKomponist): refresh data from REST server")

            val restBlechwiki = ServiceBuilder.buildService(RestInterface::class.java)
            val call = restBlechwiki.getKomponistList("Komponist", "0")
            call.enqueue(object : Callback<List<KomponistClass>> {
                override fun onResponse(
                    call: Call<List<KomponistClass>>,
                    restResponse: Response<List<KomponistClass>>
                ) {
                    Log.d("KomponistRepository", "getAllKomponist: onResponse, we got ${restResponse.body()}")
                    if (restResponse.isSuccessful) {
                        Log.d("KomponistRepository", "Response: Komponist size : ${restResponse.body()?.size}")
//                        restResponse.body()?.forEach{insertKomponist(it)}
//                        val tableListinsert: List<KomponistClass> = restResponse.filter{it.change == "new" }    // .filter{it.change == "new"}
                        if (restResponse.body()!!.isNotEmpty()) {
                            val tableListinsert: List<KomponistClass> = restResponse.body()!!
                            GlobalScope.launch { insertAllKomponist(tableListinsert) }
                            sharedPreference.save(Constant.PREF_AUTO_NR_KOMPONIST, StoreVars.instance.autoNrKomponist)
                            Log.d("KomponistRepository", "getAllKomponist: onResponse, saved Constant.PREF_AUTO_NR_KOMPONIST = StoreVars.instance.autoNrKomponist = ${StoreVars.instance.autoNrKomponist}")
                        }
                    } else {
                        Log.d("KomponistRepository", "getAllKomponist: onResponse, no success in retrieving data, ${restResponse.message()}")
                        Toast.makeText(
                            appContext, "KomponistRepository(getAllKomponist): onResponse, no success in retrieving data, ${restResponse.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<KomponistClass>>, t: Throwable) {
                    Log.d("KomponistRepository (getAllKomponist): onFailure", "Something went wrong $t")
                    Toast.makeText(
                        appContext,
                        "KomponistRepository (getAllKomponist): Error $t",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        } else {
            Log.d("KomponistRepository", "getAllKomponist: no new data available")
        }
        Log.d("KomponistRepository", "KomponistRepository: try to fetch Komponist from database")
        return mBlechDao.getAllKomponist(query)
    }

    suspend fun insertKomponist(Komponist: KomponistClass) {
        Log.v("KomponistRepository", "insertKomponist " + Komponist.komponist);
        mBlechDao.insert(Komponist);
    }

    suspend fun insertAllKomponist(komponistList: List<KomponistClass>) {
        Log.v("KomponistRepository", "insertAllKomponist " + komponistList.size);
        mBlechDao.insertAllKomponist(komponistList);
    }
    suspend fun getKomponistDetails(komponistNr: Int): MutableLiveData<List<TitelInBuchClass>> {
        Log.d("KomponistRepository", "getKomponistDetails $komponistNr")
        val tableListinsert = MutableLiveData<List<TitelInBuchClass>>() // = emptyList()

        val destinationService = ServiceBuilder.buildService(RestInterface::class.java)
        val call = destinationService.getKomponistDetails(komponistNr)
        call.enqueue(object : Callback<List<TitelInBuchClass>> {
            override fun onResponse(
                call: Call<List<TitelInBuchClass>>,
                restResponse: Response<List<TitelInBuchClass>>
            ) {
                Log.d("getKomponistDetails","onResponse: we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d("getKomponistDetails","Response: Buch size : ${restResponse.body()?.size}")
                    if (restResponse.body()!!.isNotEmpty()) {
                        tableListinsert.value = restResponse.body()
                    }
                } else {
                    Log.d("KomponistRepository", "getKomponistDetails onResponse: no success in retrieving data, ${restResponse.message()}")
                    Toast.makeText(
                        appContext, "KomponistRepository, getKomponistDetails onResponse: no success in retrieving data, ${restResponse.message()}",
                        Toast.LENGTH_SHORT
                    ).show()                }
            }

            override fun onFailure(call: Call<List<TitelInBuchClass>>, t: Throwable) {
                Log.d("KomponistRepository","getKomponistDetails: onFailure, Something went wrong $t")
                Toast.makeText(appContext, "KomponistRepository(getKomponistDetails): Error $t", Toast.LENGTH_SHORT).show()
            }
        })
        return tableListinsert
    }

    init {
        // initialize database connection
        Log.d("KomponistRepository", "init")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!

        // initialize store for global variables
        if (StoreVars.instance.autoNrKomponist == 0) {
            val autoNrViewModel = ViewModelProvider(appContext).get(AutoNrViewModel::class.java)
            autoNrViewModel.getAutoNr
            Log.d("KomponistRepository", "init: autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
    }
}
