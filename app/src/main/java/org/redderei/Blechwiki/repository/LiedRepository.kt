package org.redderei.Blechwiki.repository

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.redderei.Blechwiki.gettersetter.LiedClass
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

class LiedRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    var changecounter: Int = 0
    val sharedPreference: SharedPreference = SharedPreference(appContext)


    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    fun getAllLieder(mKirche: String, sortType: String, query: String): LiveData<List<LiedClass>>? {
        // no data there yet or part of it missing
        changecounter = sharedPreference.getValueInt(Constant.PREF_CHANGECOUNTER_LIED)
        Log.d("LiedRepository", "getAllLieder: query=>${query}<, sharedPreference Liedcounter=" +
                    "${changecounter}, mKirche=${mKirche}")
        if (changecounter == 0) {
            val restBlechwiki = ServiceBuilder.buildService(RestInterface::class.java)
            val call = restBlechwiki.getLiedList()
            call.enqueue(object : Callback<List<LiedClass>> {
                override fun onResponse(
                    call: Call<List<LiedClass>>,
                    restResponse: Response<List<LiedClass>>
                ) {
                    Log.d(
                        "LiedRepository",
                        "getAllLieder: onResponse, we got ${restResponse.body()}"
                    )
                    if (restResponse.isSuccessful) {
                        Log.d(TAG, "Response: Lieder size : ${restResponse.body()!!.size}")
                        if (restResponse.body()!!.isNotEmpty()) {
                            val tableListinsert: List<LiedClass> = restResponse.body()!!
                            GlobalScope.launch { insertAllLied(tableListinsert) }
                        }
                    } else {
                        Log.d(
                            "LiedRepository",
                            "getAllLied: onResponse, no success in retrieving data, ${restResponse.message()}"
                        )
                        Toast.makeText(
                            appContext,
                            "LiedRepository(getAllLied) onResponse, no success in retrieving data, ${restResponse.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<LiedClass>>, t: Throwable) {
                    Log.d("LiedRepository", "getAllLied: onFailure, Something went wrong $t")
                    Toast.makeText(
                        appContext,
                        "LiedRepository (getAllLied): Error $t",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        Log.d("LiedRepository", "try to fetch Lied from BlechDao, sorttype= ${sortType}")
        return when (sortType) {
            "ABC" -> {
                mBlechDao.getAllLiedSortABC(mKirche, query)
            }

            "Nr" -> {
                mBlechDao.getAllLiedSortNr(mKirche, query)
            }

            "Anlass" -> {
                mBlechDao.getAllLiedSortAnlass(mKirche, query)
            }

            else -> null
        }
    }

    fun getLiedDetails(liedNr: Int): MutableLiveData<List<TitelInBuchClass>> {
        // returns nothing as it sets LiveData<List<TitelInBuchClass>>
        val tableListinsert = MutableLiveData<List<TitelInBuchClass>>() // = emptyList()
        Log.v("LiedRepository", "getLiedDetails " + liedNr);

        val destinationService = ServiceBuilder.buildService(RestInterface::class.java)
        val call = destinationService.getLiedDetails(liedNr)
        call.enqueue(object : Callback<List<TitelInBuchClass>> {
            override fun onResponse(
                call: Call<List<TitelInBuchClass>>,
                restResponse: Response<List<TitelInBuchClass>>
            ) {
                Log.d("LiedRepository", "getLiedDetails onResponse: we got ${restResponse.body()}")
                if (restResponse.isSuccessful) {
                    Log.d("LiedRepository", "Response: Bücher size : ${restResponse.body()!!.size}")
                    if (restResponse.body()!!.isNotEmpty()) {
                        tableListinsert.value = restResponse.body()!!
                        Log.d("LiedRepository", "getLiedDetails onResponse, no new data")
                    }
                } else {
                    Log.d("LiedRepository", "getLiedDetails onResponse: no success in retrieving data, ${restResponse.message()}")
                    Toast.makeText(
                        appContext, "LiedRepository, getLiedDetails onResponse: no success in retrieving data, ${restResponse.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<TitelInBuchClass>>, t: Throwable) {
                Log.d("LiedRepository (getLiedDetails): onFailure", "Something went wrong, ERROR: $t")
                Toast.makeText(appContext, "LiedRepository (getLiedDetails): Error $t", Toast.LENGTH_SHORT).show()
            }
        })
        return tableListinsert
    }

    suspend fun insertLied(lied: LiedClass) {
        Log.v("LiedRepository", "insertLied " + lied.lied);
        mBlechDao.insert(lied);
    }

    suspend fun insertAllLied(lied: List<LiedClass>) {
        Log.v("LiedRepository", "insertAllLied " + lied.size);
        mBlechDao.insertAllLied(lied);
        changecounter = 1 // we got no datafield within Rest data
        sharedPreference.save(
            Constant.PREF_CHANGECOUNTER_LIED, changecounter)
        Log.d("LiedRepository","insertAllLied: saved Constant.PREF_CHANGECOUNTER_LIED = ${changecounter}")
    }

    init {
        // initialize database connection
        Log.d("LiedRepository", "init")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!
    }
}

