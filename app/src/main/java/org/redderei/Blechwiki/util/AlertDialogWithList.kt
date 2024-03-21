package org.redderei.Blechwiki.util

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.util.Util.Companion.context

class AlertDialogWithList {

    fun withItems(kirche: String) : String {

        val items = Constant.mBuchLang  // Array of Gesangbuecher
        val builder = AlertDialog.Builder(context!!)
        var saveItem = ""

        with(builder)
        {
            setTitle("Bitte Gesangbuch auswählen")
            setItems(items) { dialog, which ->
                Toast.makeText(context, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                saveItem = items[which]
            }
            show()
        }
        return saveItem
    }
}