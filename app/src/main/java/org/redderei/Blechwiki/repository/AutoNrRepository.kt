package org.redderei.Blechwiki.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import org.redderei.Blechwiki.gettersetter.AutoNrClass
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.StoreVars
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.util.SharedPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutoNrRepository internal constructor(app: Application) {
    private val mBlechDao: BlechDao
    val sharedPreference: SharedPreference = SharedPreference(appContext)

    /**
     * different values of sharedPreferences and StoreVars for different statuses:
     *             directly after installation (or didn't receive any values from Web):
     *                                             sharedPref = -1        StoreVars = 0
     *             new start without changes       sharedPref = lastNr    StoreVars = lastNr
     *             new start with changes in web   sharedPref = lastNr  < StoreVars
     *             sharedPref will change after successful inserting new entries
     */
    val getAutoNr: Unit
        get() {
            Log.v("AutoNrRepository", "getGetAutoNr, get: StoreVars .autoNrBuch ${StoreVars.instance.autoNrBuch}, " +
                        "autoNrKomponist=${StoreVars.instance.autoNrKomponist}, autoNrTitel=${StoreVars.instance.autoNrTitel}")
            // app didn't store preferences so far, initialize them
            if (!sharedPreference.getValueBoolean(Constant.PREF_INITIALIZED, false)) {
                Log.v("AutoNrRepository", "getGetAutoNr runs first time on machine, init values as -1")
                // Values not initialized so far
                sharedPreference.save(Constant.PREF_SORTTYPE, "ABC")
                sharedPreference.save(Constant.PREF_KIRCHE, "N")
                sharedPreference.save(Constant.PREF_AUTO_NR_LIED, -1)
                sharedPreference.save(Constant.PREF_AUTO_NR_BUCH, -1)
                sharedPreference.save(Constant.PREF_AUTO_NR_KOMPONIST, -1)
                sharedPreference.save(Constant.PREF_AUTO_NR_TITEL, -1)
            }
            if (StoreVars.instance.autoNrBuch == 0) { // query AutoNr just directly after program start
//                if (StoreVars.instance.autoNrBuch > sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH)) { // latest run of this program received lower values
                    Log.v("AutoNrRepository", "getGetAutoNr runs again on machine, " +
                            "compare local data serial PrefAutoNrBuch=" + sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH) +
                            " StoreVars.autoNrBuch" + StoreVars.instance.autoNrBuch)
                val destinationService = ServiceBuilder.buildService(RestInterface::class.java)
                //ApiInterface.RestInterface interface
                val call = destinationService.getAutoNr()
                // make network call asynchronously, read three values latest getGetAutoNr from Web
                call.enqueue(object : Callback<AutoNrClass> {
                    override fun onResponse(call: Call<AutoNrClass>, response: Response<AutoNrClass>) {
                        if (response.isSuccessful){
                            sharedPreference.save(Constant.PREF_INITIALIZED, true)
                            val AutoNrString = response.body()!!
//                            Log.d("AutoNrRepository", "getAutoNr onResponse AutoNrString= ${AutoNrString}")
                            StoreVars.instance.autoNrBuch = AutoNrString.AutoNr.get(0).lastNr         // with SOAP was lastNr +1
                            StoreVars.instance.autoNrKomponist = AutoNrString.AutoNr.get(1).lastNr
                            StoreVars.instance.autoNrTitel = AutoNrString.AutoNr.get(2).lastNr
                            Log.d("AutoNrRepository", "getGetAutoNr: autoNrBuch=" + StoreVars.instance.autoNrBuch
                                        + " autoNrKomponist=" + StoreVars.instance.autoNrKomponist + " autoNrTitel=" + StoreVars.instance.autoNrTitel)
                            //in case of different values reset it -> data are dropped and pulled again
                            if (StoreVars.instance.autoNrBuch != 0 &&
                                StoreVars.instance.autoNrBuch != sharedPreference.getValueInt(Constant.PREF_AUTO_NR_BUCH)) {
                                Toast.makeText(appContext, "Bücher haben sich geändert", Toast.LENGTH_SHORT).show()
                            }
                            if (StoreVars.instance.autoNrKomponist != 0 &&
                                StoreVars.instance.autoNrKomponist != sharedPreference.getValueInt(Constant.PREF_AUTO_NR_KOMPONIST)) {
                                Toast.makeText(appContext, "Komponisten haben sich geändert", Toast.LENGTH_SHORT).show()
                            }
                            if (StoreVars.instance.autoNrTitel != 0 &&
                                StoreVars.instance.autoNrTitel != sharedPreference.getValueInt(Constant.PREF_AUTO_NR_TITEL)) {
                                Toast.makeText(appContext, "Titel haben sich geändert", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Log.d("AutoNrRepository", "getAutoNr: onResponse, Something went wrong ${response.message()}")
                            Toast.makeText(appContext, "AutoNrRepository(getAutoNr): ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<AutoNrClass>, t: Throwable) {
                        Log.d("AutoNrRepository", "getAutoNr: onFailure, Something went wrong $t")
                        Toast.makeText(appContext, "AutoNrRepository(getAutoNr): Error $t", Toast.LENGTH_SHORT).show()
                    }
                })
//                }
            }
        }

    val getLastNr: Unit
        get() {
            Log.v("AutoNrRepository", "getLastNr")
            mBlechDao.getMaxBuchId()
        }

    init {
        // initialize database connection
        Log.d("AutoNrRepository", "init")
        val db: BlechDatabase? = BlechDatabase.getDatabase(app)
        mBlechDao = db?.BlechDao()!!

        // initialize store for global variables
        if (StoreVars.instance.autoNrBuch == 0) {
            getAutoNr
            Log.d("AutoNrRepository", "init: autoNrBuch=${StoreVars.instance.autoNrBuch} autoNrKomponist=${StoreVars.instance.autoNrKomponist} autoNrTitel=${StoreVars.instance.autoNrTitel}")
        }
    }
}
