package org.redderei.Blechwiki.gettersetter

// https://stackoverflow.com/questions/5836662/extending-from-two-classes#5836735
class TitelInBuchClass {
    var detailListItemType = 0
    var buchId: String = ""
    var buchTitel: String = ""
    var buchUntertitel: String = ""
    var zus: String = ""
    var imgUrl: String = ""
    var titel: String = ""
    var titelNr: String = ""
    var titelKomponist: String = ""
    var titelBesetzung: String = ""
    var titelVorzeichen: String = ""
    var audioURL: String = ""

    companion object {
        // possible values of detaillistitemtype
        const val egLiedFundstellen = 1
        const val buchFundstellen = 2
        const val komponistFundstellen = 3
        const val titelFundstellen = 4
    }
}