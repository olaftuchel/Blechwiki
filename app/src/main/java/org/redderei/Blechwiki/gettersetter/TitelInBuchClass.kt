package org.redderei.Blechwiki.gettersetter

// https://stackoverflow.com/questions/5836662/extending-from-two-classes#5836735
class TitelInBuchClass {
    var detailListItemType = 0
    var buch: String = ""      //ex buchTitel
    var nr: String = "" // ex titelNr
    var titel: String = ""
    var zus: String = ""
    var komponist: String = ""      // ex titelKomponist
    var besetzung: String = ""  // ex titelBesetzung
    var vorzeichen: String = "" // ex titelVorzeichen
    var titelZ: String = ""     // neu
    var buchId: String = ""
    var buchUntertitel: String = ""
    var quellekurz: String = ""
    var audioURL: String = ""

    companion object {
        // possible values of detaillistitemtype
        const val getEgLiedFundstellen = 1
        const val getBuchFundstellen = 2
        const val getKomponistFundstellen = 3
        const val getTitelFundstellen = 4
    }
}