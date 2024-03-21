package org.redderei.Blechwiki.gettersetter

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.util.concurrent.atomic.AtomicReference


/**
 * Created by ot775x on 30.08.2018.
 */
// https://stackoverflow.com/questions/8031597/alertdialog-within-options-menu
class ChoralBuch {
    private var mBuch // Ausgewählter Wert aus mBuchKurz
            : String? = null

    fun SelectBuch(context: Context?, choralbuchEntity: ChoralBuch) {
        val returnValue = AtomicReference<String>()
        // https://stackoverflow.com/questions/8304137/how-to-return-a-value-from-a-inner-class
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Select Choralbuch")
        builder.setSingleChoiceItems(ChoralBuch.Companion.mBuchLang, 0, DialogInterface.OnClickListener { dialog, item ->
            mBuch = ChoralBuch.Companion.mBuchKurz.get(item)
            choralbuchEntity.Set(mBuch)
            Toast.makeText(context, ChoralBuch.Companion.mBuchLang.get(item).toString() + " " + mBuch, Toast.LENGTH_SHORT).show()
            dialog.cancel()
        })
        val alert = builder.create()
        alert.show()
        return
    }

    fun Set(mBuch: String?) {
        this.mBuch = mBuch
    }

    fun Get(): String? {
        return mBuch
    }

    companion object {
        private val mBuchLang = arrayOf("Bayern/Thüringen", "Hessen-Nassau", "Kurhessen-Waldeck", "Mecklenburg", "Niedersachsen/Bremen", "Nordelbien", "Oldenburg",
                "Österreich", "Pfalz", "Reformierte", "Rheinland/Westfalen/Lippe", "Württemberg", "Baden/Elsaß/Lothringen", "alle Anhänge")
        private val mBuchKurz = arrayOf("BT", "HN", "KW", "M", "NB", "N", "Ol", "Ö", "P", "RW", "RW", "W", "BE", "alleAnhänge")
    }
}