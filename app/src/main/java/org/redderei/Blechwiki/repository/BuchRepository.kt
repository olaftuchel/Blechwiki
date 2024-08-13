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
    var changecounter: Int = 0
    val sharedPreference: SharedPreference = SharedPreference(appContext)


    suspend fun getAllBuch(query: String): LiveData<List<BuchClass>>? {
        // no data there yet or part of it missing
        changecounter = sharedPreference.getValueInt(Constant.PREF_CHANGECOUNTER_BUCH)
        Log.d("BuchRepository", "getAllBuch: query=>${query}<, sharedPreference BuchCounter=" +
                "${changecounter}")

        val restBlechwiki = ServiceBuilder.buildService(RestInterface::class.java)
        val call = restBlechwiki.getBuchList("Buch", changecounter.toString())
        call.enqueue(object : Callback<List<BuchClass>> {
            override fun onResponse(
                call: Call<List<BuchClass>>,
                restResponse: Response<List<BuchClass>>
            ) {
                Log.d("BuchRepository", "getAllBuch: onResponse, we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d("BuchRepository", "getAllBuch: Buch size : ${restResponse.body()?.size}")
                    if (restResponse.body()!!.isNotEmpty()) {
                        val tableListinsert: List<BuchClass> = restResponse.body()!!
                        GlobalScope.launch { modifyAllBuch(changecounter, tableListinsert) }
                    } else {
                        Log.d("BuchRepository", "getAllBuch: onResponse, no new data")
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

        Log.d("BuchRepository", "fetch Buch from database")
        return mBlechDao.getAllBuch(query)
    }

    suspend fun modifyAllBuch(changecounter: Int, buch: List<BuchClass>) {
        Log.v("BuchRepository", "modifyAllBuch, changecounter = ${changecounter}, size= ${buch.size}");
        if (changecounter == 0) {      // initial call
            mBlechDao.insertBuch(buch);
        } else {                // update, delete, add
            deleteBuch(buch.filter{it.change == "delete"})
            newBuch(buch.filter{it.change == "new"})
            updateBuch(buch.filter{it.change == "update"})
        }
        // maximum value of last changecounter
        val newchangecounter = buch.maxOf{p -> p.changecounter}
        Log.v("BuchRepository", "modifyAllBuch, newchangecounter = ${newchangecounter}");
        sharedPreference.save(Constant.PREF_CHANGECOUNTER_BUCH, newchangecounter)
    }

    suspend fun newBuch(buch: List<BuchClass>) {
        Log.v("BuchRepository", "newBuch, size: " + buch.size);
        if (buch.size > 0) {
            mBlechDao.insertBuch(buch);
        }
    }

    suspend fun deleteBuch(buch: List<BuchClass>) {
        Log.v("BuchRepository", "deleteBuch, size: " + buch.size);
        if (buch.size > 0) {
            mBlechDao.deleteBuch(buch);
        }
    }

    suspend fun updateBuch(buch: List<BuchClass>) {
        Log.v("BuchRepository", "updateBuch, size: " + buch.size);
        if (buch.size > 0) {
            mBlechDao.updateBuch(buch);
        }
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
        Log.d("BuchRepository", "init")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!
/*
        // initialize store for global variables
        if (StoreVars.instance.autoNrBuch == 0) {
            val autoNrViewModel = ViewModelProvider(appContext).get(AutoNrViewModel::class.java)
            autoNrViewModel.getAutoNr
            Log.d("BuchRepository", "init: StoreVars, autoNrBuch=${StoreVars.instance.autoNrBuch} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
*/    }
}
