package org.redderei.Blechwiki.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.redderei.Blechwiki.gettersetter.KomponistClass
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
    var changecounter: Int = 0

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    suspend fun getAllKomponist(query: String): LiveData<List<KomponistClass>>? {
        // no data there yet or part of it missing
        changecounter = sharedPreference.getValueInt(Constant.PREF_CHANGECOUNTER_KOMPONIST)
        Log.d("KomponistRepository", "getAllKomponist: query=>${query}< sharedPreference KomponistCounter="
                    + "${changecounter}")
        val restBlechwiki = ServiceBuilder.buildService(RestInterface::class.java)
        val call = restBlechwiki.getKomponistList("Komponist", changecounter.toString())
        call.enqueue(object : Callback<List<KomponistClass>> {
            override fun onResponse(
                call: Call<List<KomponistClass>>,
                restResponse: Response<List<KomponistClass>>
            ) {
                Log.d("KomponistRepository", "getAllKomponist: onResponse, we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d("KomponistRepository", "Response: Komponist size : ${restResponse.body()?.size}")
                    if (restResponse.body()!!.isNotEmpty()) {
                        val tableListinsert: List<KomponistClass> = restResponse.body()!!
                        GlobalScope.launch { modifyAllKomponist(changecounter, tableListinsert)}
                    } else {
                        Log.d("KomponistRepository", "getAllKomponist: onResponse, no new data")
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
                    appContext,"KomponistRepository (getAllKomponist): Error $t",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        Log.d("KomponistRepository", "KomponistRepository: fetch Komponist from database")
        return mBlechDao.getAllKomponist(query)
    }

    suspend fun modifyAllKomponist(changecounter: Int, komponist: List<KomponistClass>) {
        Log.v("KomponistRepository", "modifyAllKomponist, changecounter = ${changecounter}, size= ${komponist.size}");
        if (changecounter == 0) {      // initial call
            mBlechDao.insertKomponist(komponist);
        } else {                // update, delete, add
            deleteKomponist(komponist.filter{it.change == "delete"})
            newKomponist(komponist.filter{it.change == "new"})
            updateKomponist(komponist.filter{it.change == "update"})
        }
        // maximum value of last changecounter
        val newchangecounter = komponist.maxOf{p -> p.changecounter}
        Log.v("KomponistRepository", "modifyAllKomponist, newchangecounter = ${changecounter}");
        sharedPreference.save(Constant.PREF_CHANGECOUNTER_KOMPONIST, newchangecounter)
    }

    suspend fun newKomponist(Komponist: List<KomponistClass>) {
        Log.v("KomponistRepository", "newKomponist, size: " + Komponist.size);
        if (Komponist.size > 0) {
            mBlechDao.insertKomponist(Komponist);
        }
    }

    suspend fun deleteKomponist(Komponist: List<KomponistClass>) {
        Log.v("KomponistRepository", "deleteKomponist, size: " + Komponist.size);
        if (Komponist.size > 0) {
            mBlechDao.deleteKomponist(Komponist);
        }
    }

    suspend fun updateKomponist(Komponist: List<KomponistClass>) {
        Log.v("KomponistRepository", "updateKomponist, size: " + Komponist.size);
        if (Komponist.size > 0) {
            mBlechDao.updateKomponist(Komponist);
        }
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
                    Log.d("getKomponistDetails","Response: Komponist size : ${restResponse.body()?.size}")
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
/*
        // initialize store for global variables
        if (StoreVars.instance.autoNrKomponist == 0) {
            val autoNrViewModel = ViewModelProvider(appContext).get(AutoNrViewModel::class.java)
            autoNrViewModel.getAutoNr
            Log.d("KomponistRepository", "init: autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
*/    }
}
