package org.redderei.Blechwiki.gettersetter

// Runtime storage of latest REST API numbers
class StoreVars private constructor(){
    var autoNrBuch: Int = 0
    var autoNrKomponist: Int = 0
    var autoNrTitel: Int = 0

    companion object{
        val instance = StoreVars()
    }
}