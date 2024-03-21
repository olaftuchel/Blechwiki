package org.redderei.Blechwiki.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager


/**
 * Created by ot775x on 18.03.2018.
 */
class Util : Application() {

    // This part (onCreate, getInstance, getContext) is for global Utility to get context from everywhere
    // https://stackoverflow.com/questions/9445661/how-to-get-the-context-from-anywhere
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
    }

    companion object {      // all the blow objects can be addressed as Util.isNetworkAvailable and so on
//        var instance: Util? = null      // https://stackoverflow.com/questions/37391221/kotlin-singleton-application-class
//            private set
        private lateinit var instance: Util
        val context: Context?
            get() = instance

        fun applicationContext(): Util {
            return instance as Util
        }

        // Checking network (before start)

        fun isNetworkAvailable(): Boolean {

            val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetwork
            return activeNetwork != null   // return value
        }

    }
}