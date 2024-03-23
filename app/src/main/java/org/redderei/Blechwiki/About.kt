package org.redderei.Blechwiki

import android.app.Activity
import android.os.Bundle
import org.redderei.Blechwiki.R

class About : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
    }
}