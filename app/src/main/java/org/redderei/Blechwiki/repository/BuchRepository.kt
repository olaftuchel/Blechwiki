package org.redderei.Blechwiki.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.BuchClass
import org.redderei.Blechwiki.gettersetter.StoreVars
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import org.redderei.Blechwiki.util.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuchRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    val sharedPreference: SharedPreference = SharedPreference(appContext)

    suspend fun getAllBuch(query: String): LiveData<List<BuchClass>>? {
        Log.d("BuchRepository", "getAllBuch: query=>${query}<, sharedPreference AutoNrBuch=" +
                "${sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH)}, StoreVars.autoNrBuch=${StoreVars.instance.autoNrBuch}")

        // no data there yet or part of it missing
        if (sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH) < StoreVars.instance.autoNrBuch) {
            Log.d("BuchRepository", "getAllBuch: refresh data from REST server")

            val destinationService = ServiceBuilder.buildService(RestInterface::class.java)
            val call = destinationService.getBuchList("Buch", "0")
            // works well
//            val destinationService = ServiceBuilder.buildService(RestGetBuchList::class.java)
//            val call = destinationService.getBuchList()
            call.enqueue(object : Callback<List<BuchClass>> {
                override fun onResponse(
                    call: Call<List<BuchClass>>,
                    restResponse: Response<List<BuchClass>>
                ) {
                    Log.d("BuchRepository", "getAllBuch: onResponse, we got ${restResponse.body()}")
                    if (restResponse.isSuccessful) {
                        Log.d("BuchRepository", "getAllBuch: Buch size : ${restResponse.body()?.size}")
//                        restResponse.body()?.forEach{insertBuch(it)}
//                        val tableListinsert: List<BuchClass> = restResponse.filter{it.change == "new" }    // .filter{it.change == "new"}
                        if (restResponse.body()!!.isNotEmpty()) {
                            val tableListinsert: List<BuchClass> = restResponse.body()!!
                            GlobalScope.launch { insertAllBuch(tableListinsert) }
                            sharedPreference.save(Constant.PREF_AUTO_NR_BUCH, StoreVars.instance.autoNrBuch)
                            Log.d("BuchRepository", "getAllBuch: onResponse, saved Constant.PREF_AUTO_NR_BUCH = StoreVars.instance.autoNrBuch = ${StoreVars.instance.autoNrBuch}")
                        }
                    } else {
                        Log.d("BuchRepository", "getAllBuch: onResponse, no success in retrieving data, ${restResponse.message()}")
                        Toast.makeText(
                            appContext, "BuchRepository(getAllBuch) onResponse, no success in retrieving data, ${restResponse.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<BuchClass>>, t: Throwable) {
                    Log.d("BuchRepository", "getAllBuch: onFailure, Something went wrong $t")
                    Toast.makeText(
                        appContext,
                        "getAllBuch): Error $t",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
        Log.d("BuchRepository", "try to fetch Buch from database")
        return mBlechDao.getAllBuch(query)
    }

    suspend fun insertBuch(buch: BuchClass) {
        Log.v("BuchRepository", "insertBuch) " + buch.buch);
        mBlechDao.insert(buch);
    }

    suspend fun insertAllBuch(buchList: List<BuchClass>) {
        Log.v("BuchRepository", "insertAllBuch, size= " + buchList.size);
        mBlechDao.insertAllBuch(buchList);
    }

    suspend fun getBuchDetails(buchNr: Int): MutableLiveData<List<TitelInBuchClass>> {
        Log.d("BuchRepository", "getBuchDetails, buchNr= $buchNr")
        val tableListinsert = MutableLiveData<List<TitelInBuchClass>>() // = emptyList()

        val destinationService = ServiceBuilder.buildService(RestInterface::class.java)
        val call = destinationService.getBuchDetails(buchNr)
        call.enqueue(object : Callback<List<TitelInBuchClass>> {
            override fun onResponse(
                call: Call<List<TitelInBuchClass>>,
                restResponse: Response<List<TitelInBuchClass>>
            ) {
                Log.d("getBuchDetails",  "onResponse: we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d("getBuchDetails", "Response: Buch size= ${restResponse.body()?.size}")
                    if (restResponse.body()!!.isNotEmpty()) {
                        tableListinsert.value = restResponse.body()
                    }
                } else {
                    Log.d("BuchRepository", "getBuchDetails: onResponse, no success in retrieving data, ${restResponse.message()}")
                    Toast.makeText(
                        appContext, "BuchRepository, getBuchDetails: onResponse, no success in retrieving data, ${restResponse.message()}",
                        Toast.LENGTH_SHORT
                    ).show()                }
            }

            override fun onFailure(call: Call<List<TitelInBuchClass>>, t: Throwable) {
                Log.d("BuchRepository","getBuchDetails: onFailure, Something went wrong $t")
                Toast.makeText(appContext, "BuchRepository(getBuchDetails): Error $t", Toast.LENGTH_SHORT).show()
            }
        })
        return tableListinsert
    }

    init {
        // initialize database connection
        Log.d("BuchRepository", "init)")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!

        // initialize store for global variables
        if (StoreVars.instance.autoNrBuch == 0) {
            val autoNrViewModel = ViewModelProvider(appContext).get(AutoNrViewModel::class.java)
            autoNrViewModel.getAutoNr
            Log.d("BuchRepository", "init: StoreVars, autoNrBuch=${StoreVars.instance.autoNrBuch} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
    }
}

