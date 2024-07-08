package org.redderei.Blechwiki.gettersetter

/**
 * Created by ot775x on 27.01.2018.
 */
object Constant {
    const val PREF_INITIALIZED = "initialized"
    const val PREF_NAME = "name"
    const val PREF_KIRCHE = "Kirche"
    const val PREF_SORTTYPE = "Sorttype"
    const val PREF_AUTO_NR_LIED = "AutoNrLied"
    const val PREF_AUTO_NR_BUCH = "AutoNrBuch"
    const val PREF_AUTO_NR_KOMPONIST = "AutoNrKomponist"
    const val PREF_AUTO_NR_TITEL = "AutoNrTitel"
    const val PREF_CHANGECOUNTER_BUCH = "ChangecounterBuch"
    const val PREF_CHANGECOUNTER_KOMPONIST = "ChangecounterKomponist"
    const val PREF_CHANGECOUNTER_TITEL = "ChangecounterTitel"
    // need same order of arrays below
    val mBuchLang = arrayOf("Baden/Elsaß/Lothringen", "Bayern/Thüringen", "Hessen-Nassau", "Kurhessen-Waldeck", "Mecklenburg", "Niedersachsen/Bremen", "Nordelbien", "Oldenburg",
            "Österreich", "Pfalz", "Reformierte", "Rheinland/Westfalen/Lippe", "Württemberg", "alle Anhänge")
    var mBuchKurz = arrayOf("BEL", "BT", "HN", "KW", "M", "NB", "N", "Ol", "Ö", "P", "R", "RWL", "W", "alleAnhänge")
    var mThema = arrayOf("Abend", "Abendmahl", "Advent", "Angst und Vertrauen", "Arbeit", "Auf Reisen", "Beichte", "Biblische Erzähllieder", "Bußtag",
            "Eingang und Ausgang", "Ende des Kirchenjahres", "Epiphanias", "Erhaltung der Schöpfung", "Geborgen in Gottes Liebe",
            "Himmelfahrt", "Jahreswende", "Johannestag, 24.Juni", "Liturgische Gesänge", "Loben und Danken", "Michaelistag, 29.September",
            "Mittag und das täglich Brot", "Morgen", "Nächsten-und Feindesliebe", "Natur und Jahreszeiten", "Ökumene",
            "Ostern", "Passion", "Pfingsten", "Psalmen und Lobgesänge", "Rechtfertigung und Zuversicht", "Sammlung und Sendung",
            "Sterben und ewiges Leben", "Taufe und Konfirmation", "Trauung", "Trinitatis", "Umkehr und Nachfolge", "Weihnachten", "Wort Gottes")

    // Rest information
    const val restURL = "http://pcportal.ddns.net/RestBlechWiki/api/"
    const val imgURL = "http://pcportal.ddns.net/Bilder/BilderBuch/"
    const val miniImgURL = "http://pcportal.ddns.net/Bilder/BilderBuch/mini240/"

    // DB variables
    const val DB_NAME = "blechwiki.db"
    const val DB_VERSION = 2

    const val TABLE_LIED = "lied_table"
    const val LIED = "lied"
    const val IX = "ix"
    const val IXUR = "ixur"
    const val TEIL = "teil"
    const val NR = "nr"
    const val ANLASS = "anlass"
    // for all 3 tables
    const val ID = "id"
    const val CHANGECOUNTER = "changecounter"
    const val CHANGE = "change"

    const val TABLE_BUCH = "buch_table"
    const val BUCHID = "buchId"
    const val BUCHKURZ = "buchkurz"
    const val BUCH = "buch"
    const val UNTERTITEL = "buchuntertitel"
    const val ERSCHEINJAHR = "erscheinjahr"
    const val HERAUSGEBER = "herausgeber"
    const val HERAUSG_VORNAME = "herausg_vorname"
    const val VERLAG = "verlag"
    const val VERLAGSNUMMER = "verlagsnummer"
    const val ZULIEFERUNG = "zulieferung"
    const val RELEVANZ = "relevanz"
    const val VORHANDEN = "vorhanden"
    const val IMGURL = "imgurl"

    const val TABLE_KOMPONIST = "komponist_table"
    const val KURZ = "kurz"
    const val KOMPONIST = "komponist"
    const val FRIENDLYKOMPONISTNAME = "friendly_komponist_name"

    const val TABLE_TITEL = "titel_table"
    const val TITELOHNEKOMMA = "titel_ohne_komma"
    const val TITEL = "titel"
//    const val IX = "ix"

    const val TITELNR = "titelNr"
    const val TITELKOMPONIST = "titelKomponist"
    const val TITELBESETZUNG = "titelBesetzung"
    const val TITELVORZEICHEN = "titelVorzeichen"
    const val TABLE_TITEL_IN_BUCH = "titel_in_buch_table"
    const val DETAILLISTITEMTYPE = "detail_list_item_type"

    const val TABLE_STORE = "store"
    const val LAST_AUTONR_BUCH = "storeBuch"
    const val LAST_AUTONR_KOMPONIST = "storeKomponist"
    const val LAST_AUTONR_TITEL = "storeTitel"
}