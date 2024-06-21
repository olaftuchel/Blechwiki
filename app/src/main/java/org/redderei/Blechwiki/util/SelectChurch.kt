package org.redderei.Blechwiki.util

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.redderei.Blechwiki.MainActivity
import org.redderei.Blechwiki.gettersetter.Constant

class SelectChurch {
    private val sharedPreference: SharedPreference = SharedPreference(MainActivity.appContext)

    fun withItems(): String {

        val itemsLang = Constant.mBuchLang  // Array of Gesangbuecher
        val itemsKurz = Constant.mBuchKurz
        val builder = AlertDialog.Builder(MainActivity.appContext)
        var saveItem = ""

        with(builder)
        {
            setTitle("Bitte Gesangbuch auswählen")
            setItems(itemsLang) { dialog, which ->
                Toast.makeText(context, itemsLang[which] + " is clicked", Toast.LENGTH_SHORT).show()
                saveItem = itemsKurz[which]
            }
            show()
        }
        return saveItem
    }

    fun selectAndFilterChurch() {
        val mKircheAwait = selectAsync()
        runBlocking {
            val mKirche = mKircheAwait.await()
            sharedPreference.save(Constant.PREF_KIRCHE, mKirche)
            Log.v(ContentValues.TAG, "SelectChurch: mKirche=$mKirche selected")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun selectAsync() = GlobalScope.async {
        withItems()
    }
}