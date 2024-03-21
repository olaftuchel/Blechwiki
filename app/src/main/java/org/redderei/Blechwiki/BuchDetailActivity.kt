package org.redderei.Blechwiki

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

/*
    Show detailed information about Buch
 */
class BuchDetailActivity : AppCompatActivity() {
    private val mInflater: LayoutInflater? = null
    var v: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(ContentValues.TAG, "BuchDetailActivity: (onCreate)")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buch_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        val thumbnailImageView = findViewById<View>(R.id.buch_list_thumbnail) as ImageView
        val titelmain = findViewById<View>(R.id.titel_main) as TextView
        val titeluntertitel = findViewById<View>(R.id.titel_untertitel) as TextView
        val titelherausgeber = findViewById<View>(R.id.titel_herausgeber) as TextView
        val titelherausgebervorname = findViewById<View>(R.id.titel_herausgebervorname) as TextView
        val titelverlag = findViewById<View>(R.id.titel_verlag) as TextView
        val titelverlagsnummer = findViewById<View>(R.id.titel_verlagsnummer) as TextView
        val titelerscheinjahr = findViewById<View>(R.id.title_erscheinjahr) as TextView
        titelmain.text = intent.getStringExtra(ARG_ITEM_BUCH)
        titeluntertitel.text = intent.getStringExtra(ARG_ITEM_UNTERTITEL)
        titelherausgeber.text = intent.getStringExtra(ARG_ITEM_HERAUSGEBER)
        titelherausgebervorname.text = intent.getStringExtra(ARG_ITEM_HERAUSGEBERVORNAME)
        titelverlag.text = intent.getStringExtra(ARG_ITEM_VERLAG)
        titelverlagsnummer.text = intent.getStringExtra(ARG_ITEM_VERLAGSNUMMER)
        titelerscheinjahr.text = intent.getStringExtra(ARG_ITEM_ERSCHEINJAHR)
        val thumbnailImage = intent.getStringExtra(ARG_ITEM_IMGURL)


        // 3 Making use of the open-source Picasso library for asynchronous image loading
        // -- it helps you download the thumbnail images on a separate thread instead of
        // the main thread. You're also assigning a temporary placeholder for the ImageView to handle slow loading of images.
        Picasso.get().load(thumbnailImage).placeholder(R.drawable.keinbild).into(thumbnailImageView)
        return
    }

    companion object {
        const val ARG_ITEM_BUCH = "ARG_ITEM_BUCH"
        const val ARG_ITEM_UNTERTITEL = "ARG_ITEM_UNTERTITEL"
        const val ARG_ITEM_ERSCHEINJAHR = "ARG_ITEM_ERSCHEINJAHR"
        const val ARG_ITEM_HERAUSGEBER = "ARG_ITEM_HERAUSGEBER"
        const val ARG_ITEM_HERAUSGEBERVORNAME = "ARG_ITEM_HERAUSGEBERVORNAME"
        const val ARG_ITEM_VERLAG = "ARG_ITEM_VERLAG"
        const val ARG_ITEM_VERLAGSNUMMER = "ARG_ITEM_VERLAGSNUMMER"
        const val ARG_ITEM_IMGURL = "ARG_ITEM_IMGURL"
    }
}