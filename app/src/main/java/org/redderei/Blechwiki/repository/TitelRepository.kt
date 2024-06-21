package org.redderei.Blechwiki.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.TitelClass
import org.redderei.Blechwiki.gettersetter.StoreVars
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import org.redderei.Blechwiki.util.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TitelRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    val sharedPreference: SharedPreference = SharedPreference(appContext)

    suspend fun getAllTitel(query: String): LiveData<List<TitelClass>> {
        Log.d("TitelRepository", "getAllTitel: query=>${query}<, sharedPreference AutoNrTitel=" +
                    "${sharedPreference.getValueInt(Constant.PREF_AUTO_NR_TITEL)}, StoreVars.autoNrTitel=${StoreVars.instance.autoNrTitel}")

        // no data there yet or part of it missing
        if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_TITEL) < StoreVars.instance.autoNrTitel) {
            Log.d("TitelRepository", "getAllTitel: refresh data from REST server")

            val restBlechwiki = ServiceBuilder.buildService(RestInterface::class.java)
            val call = restBlechwiki.getTitelList("Titel", "0")
            call.enqueue(object : Callback<List<TitelClass>> {
                override fun onResponse(
                    call: Call<List<TitelClass>>,
                    restResponse: Response<List<TitelClass>>
                ) {
                    Log.d("TitelRepository", "getAllTitel onResponse: we got ${restResponse.body()}")
                    if (restResponse.isSuccessful) {
                        Log.d("TitelRepository", "Response: Titel size : ${restResponse.body()?.size}")
//                        restResponse.body()?.forEach{insertTitel(it)}
//                        val tableListinsert: List<TitelClass> = restResponse.filter{it.change == "new" }    // .filter{it.change == "new"}
                        val tableListinsert: List<TitelClass> = restResponse.body()!!
                        GlobalScope.launch { insertAllTitel(tableListinsert) }
                        sharedPreference.save(Constant.PREF_AUTO_NR_TITEL, StoreVars.instance.autoNrTitel)
                        Log.d("TitelRepository", "getAllTitel onResponse: saved Constand.PREF_AUTO_NR_TITEL = StoreVars.instance.autoNrTitel = ${StoreVars.instance.autoNrTitel}")
                    } else {
                        Log.d("TitelRepository", "getAllTitel onResponse: no success in retrieving data, ${restResponse.message()}")
                        Toast.makeText(
                            appContext,"TitelRepository(getAllTitel) onResponse: no success in retrieving data, ${restResponse.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<TitelClass>>, t: Throwable) {
                    Log.d("TitelRepository",  "getAllTitel onFailure: Something went wrong $t")
                    Toast.makeText(
                        appContext, "TitelRepository(getAllTitel): Error $t",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
        Log.d( "TitelRepository", "try to fetch Titel from database")
        return mBlechDao.getAllTitel(query)
    }

    suspend fun insertTitel(titel: TitelClass) {
        Log.v("TitelRepository", "insertTitel " + titel.titel);
        mBlechDao.insert(titel);
    }

    suspend fun insertAllTitel(titelList: List<TitelClass>) {
        Log.v("TitelRepository", "insertAllTitel " + titelList.size);
        mBlechDao.insertAllTitel(titelList);
    }

    /*
    suspend fun getChangeTitel(changeCounter: Int) {
        Log.d("TitelRepository", "getChangeTitel)")
        val restBlechwiki = ServiceBuilder.buildService(GetRestBlechwiki::class.java)
        val call = restBlechwiki.search("RestBlechWiki/api/Titel&counter=${changeCounter}")
        call.enqueue(object : Callback<List<TitelClass>> {
            override fun onResponse(
                call: Call<List<TitelClass>>,
                restResponse: Response<List<TitelClass>>
            ) {
                Log.d(TAG, "getChangeTitel onResponse: we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d(TAG, "Response: Titel size : ${restResponse.body()?.size}")
                    val tableListinsert: List<TitelClass>? = restResponse.body()
                    GlobalScope.launch { tableListinsert?.forEach { insertTitel(it) } }
                    sharedPreference.save(Constant.PREF_AUTO_NR_TITEL, StoreVars.instance.autoNrTitel)
                } else {
                    Log.d(
                        TAG,
                        "getGetAutoNr: onResponse: Something went wrong ${restResponse.message()}"
                    )
                    Toast.makeText(
                        appContext, "TitelRepository(getGetAutoNr): ${restResponse.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<TitelClass>>, t: Throwable) {
                Log.d("getTitelList: onFailure", "Something went wrong $t")
                Toast.makeText(
                    appContext,
                    "TitelRepository(getTitelList): Error $t",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
*/
    fun getTitelDetails(titelNr: Int): MutableLiveData<List<TitelInBuchClass>> {   //: returns nothing as it sets LiveData<List<TitelInBuchClass>>
        Log.v("TitelRepository", "getTitelDetails " + titelNr);
        val tableListinsert = MutableLiveData<List<TitelInBuchClass>>() // = emptyList()

        val destinationService = ServiceBuilder.buildService(RestInterface::class.java)
        val call = destinationService.getTitelDetails(titelNr)
        call.enqueue(object : Callback<List<TitelInBuchClass>> {
            override fun onResponse(
                call: Call<List<TitelInBuchClass>>,
                restResponse: Response<List<TitelInBuchClass>>
            ) {
                Log.d("TitelRepository", "getTitelDetails onResponse: we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d("TitelRepository", "Response: Titel size : ${restResponse.body()!!.size}")
                    if (restResponse.body()!!.isNotEmpty()) {
                        tableListinsert.value = restResponse.body()!!
                    }
                } else {
                    Log.d("TitelRepository", "getTitelDetails onResponse: no success in retrieving data, ${restResponse.message()}")
                    Toast.makeText(
                        appContext, "TitelRepository, getTitelDetails onResponse: no success in retrieving data, ${restResponse.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<TitelInBuchClass>>, t: Throwable) {
                Log.d("TitelRepository (getTitelDetails): onFailure", "Something went wrong, ERROR: $t")
                Toast.makeText(appContext, "TitelRepository (getTitelDetails): Error $t", Toast.LENGTH_SHORT).show()
            }
        })
        return tableListinsert
    }

    init {
        // initialize database connection
        Log.d("TitelRepository", "init")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!
        // initialize store for global variables
        if (StoreVars.instance.autoNrTitel == 0) {
            val autoNrViewModel = ViewModelProvider(appContext).get(AutoNrViewModel::class.java)
            autoNrViewModel.getAutoNr
            Log.d("TitelRepository", "init: autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
    }
}
