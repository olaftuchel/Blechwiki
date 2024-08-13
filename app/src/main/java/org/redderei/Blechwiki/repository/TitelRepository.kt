package org.redderei.Blechwiki.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.TitelClass
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import org.redderei.Blechwiki.util.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TitelRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    var changecounter: Int = 0
    val sharedPreference: SharedPreference = SharedPreference(appContext)


    suspend fun getAllTitel(query: String): LiveData<List<TitelClass>> {
        // no data there yet or part of it missing
        changecounter = sharedPreference.getValueInt(Constant.PREF_CHANGECOUNTER_TITEL)
        Log.d("TitelRepository", "getAllTitel: query=>${query}<, sharedPreference TitelCounter=" +
                    "${changecounter}")

        val restBlechwiki = ServiceBuilder.buildService(RestInterface::class.java)
        val call = restBlechwiki.getTitelList("Titel", changecounter.toString())
        call.enqueue(object : Callback<List<TitelClass>> {
            override fun onResponse(
                call: Call<List<TitelClass>>,
                restResponse: Response<List<TitelClass>>
            ) {
                Log.d("TitelRepository", "getAllTitel onResponse: we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d("TitelRepository", "Response: Titel size : ${restResponse.body()?.size}")
                    if (restResponse.body()!!.isNotEmpty()) {
                        val tableListinsert: List<TitelClass> = restResponse.body()!!
                        GlobalScope.launch { modifyAllTitel(changecounter, tableListinsert) }
                    } else {
                        Log.d("TitelRepository", "getAllTitel onResponse: no new data")
                    }
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
        Log.d( "TitelRepository", "fetch Titel from database")
        return mBlechDao.getAllTitel(query)
    }

    suspend fun modifyAllTitel(changecounter: Int, titel: List<TitelClass>) {
        Log.v("TitelRepository", "modifyAllTitel, changecounter = ${changecounter}, size= ${titel.size}");
        if (changecounter == 0) {      // initial call
            mBlechDao.insertTitel(titel);
        } else {                // update, delete, add
            deleteTitel(titel.filter{it.change == "delete"})
            newTitel(titel.filter{it.change == "new"})
            updateTitel(titel.filter{it.change == "update"})
        }
        // maximum value of last changecounter
        val newchangecounter = titel.maxOf{p -> p.changecounter}
        Log.v("BuchRepository", "modifyAllBuch, newchangecounter = ${newchangecounter}");
        sharedPreference.save(Constant.PREF_CHANGECOUNTER_TITEL, newchangecounter)
    }

    suspend fun newTitel(Titel: List<TitelClass>) {
        Log.v("TitelRepository", "newTitel, size: " + Titel.size);
        if (Titel.size > 0) {
            mBlechDao.insertTitel(Titel);
        }
    }

    suspend fun deleteTitel(Titel: List<TitelClass>) {
        Log.v("TitelRepository", "deleteTitel, size: " + Titel.size);
        if (Titel.size > 0) {
            mBlechDao.deleteTitel(Titel);
        }
    }

    suspend fun updateTitel(Titel: List<TitelClass>) {
        Log.v("TitelRepository", "updateTitel, size: " + Titel.size);
        if (Titel.size > 0) {
            mBlechDao.updateTitel(Titel);
        }
    }

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
    }
}
