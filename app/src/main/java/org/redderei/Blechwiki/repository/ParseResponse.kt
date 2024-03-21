package org.redderei.Blechwiki.repository

import android.content.ContentValues
import android.util.Log
import org.redderei.Blechwiki.gettersetter.*
import retrofit2.Response

object ParseResponse {
    /** Parsing soap object response to get a list of lied, buch, komponist, titel
     *
     *
     * @param restResponse: String
     * @return LiedClass or BuchClass
     */


    // former     static public List<LiedClass> lied(SoapObject restResponse, String clickAction, List<LiedClass> mLiedList) {
    fun lied(restResponse: String, clickAction: String, mBlechDao: BlechDao, mLiedList: List<LiedClass>) {
        Log.d(ContentValues.TAG, "ParseResponse(Lied)")
/*
        if (clickAction === "GetEGLieder2") {
            mBlechDao.deleteAllLieder()
            val entityList: MutableList<LiedClass> = ArrayList()
            val entity = LiedClass(0, "", "", "", "", "", "")
            // whole file ends at dataset.getPropertyCount()
            val propertyCount = restResponse.propertyCount
            for (i in 0 until propertyCount) {
                val dataset = restResponse.getProperty(i) as SoapObject
                var ix = "0"
                var ixUr = "0"
                if (dataset.getPropertyAsString("Ix") != null) {
                    ix = dataset.getPropertyAsString("Ix")
                    ixUr = dataset.getPropertyAsString("IxUr")
                } else {
                    Log.d(ContentValues.TAG, "Dataset got no Ix:" + dataset.getPropertyAsString("Lied"))
                }
                val lied = dataset.getPropertyAsString("Lied")
                val teil = dataset.getPropertyAsString("Teil")
                val nr = dataset.getPropertyAsString("Nr")
                val anlass = dataset.getPropertyAsString("Anlass")
                if (i % 100 == 0) {
                    Log.v(ContentValues.TAG, "ParseResponse(Lied) Lied: $lied Teil: >$teil< Nr: $nr Ix: $ix IxUr: $ixUr Anlass: $anlass")
                }
                entity.ix = ix
                entity.ixUr = ixUr
                entity.lied = lied
                entity.teil = teil
                entity.nr = nr
                entity.anlass = anlass
                // add to list after all
                //mBlechDao.insert(entity);
                entityList.add(entity)
            }
            mBlechDao.insertAllLieder(entityList)
            entityList.removeAll(entityList)
            Util.Companion.setPreferences(Constant.PREF_AUTO_NR_LIED, "initialized")
            return
        } else {
            Log.d(ContentValues.TAG, "ParseResponse(Lied) ERROR Non-existent clickAction found:$clickAction")
        }
        
 */
        return
    }

    fun buch(restResponse: Response<List<BuchClass>>, clickAction: String, mBlechDao: BlechDao) {
        Log.d(ContentValues.TAG, "ParseResponse(Buch)")
/*
        if (clickAction === "GetBücher") {
//            mBlechDao.deleteAllBuecher()

            val entityList: MutableList<BuchClass> = ArrayList()
            val entity = BuchClass(0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
            val propertyCount = entityList.count()
            for (i in 0 until propertyCount) {   // whole file ends at dataset.getPropertyCount()
                val dataset:BuchClass = restResponse.get(i) as BuchClass
                //Log.v(TAG, dataset.toString());
                entity.BuchId = dataset.get(i).PropertyAsString("BuchId")
                entity.Buchkurz = dataset.getPropertyAsString("Buchkurz")
                entity.Untertitel = dataset.getPropertyAsString("Untertitel")
                entity.Erscheinjahr = dataset.getPropertyAsString("Erscheinjahr")
                entity.Herausgeber = dataset.getPropertyAsString("Herausgeber")
                entity.Herausg_vorname = dataset.getPropertyAsString("Herausg_vorname")
                entity.Verlag = dataset.getPropertyAsString("Verlag")
                entity.Verlagsnummer = dataset.getPropertyAsString("Verlagsnummer")
                entity.Zulieferung = dataset.getPropertyAsString("Zulieferung")
                entity.Relevanz = dataset.getPropertyAsString("Relevanz")
                entity.Relevanz = dataset.getPropertyAsString("Relevanz")
                entity.imgUrl = dataset.getPropertyAsString("imgUrl")
                /**
                 * This is because pictures are too large for listview (reversed in BuchDetailFragment)
                 */
                entity.imgUrl = entity.imgUrl.replace("/mini60", "/mini240")
                entity.changecounter = dataset.getPropertyAsString("changecounter")
                entity.change = dataset.getPropertyAsString("change")

                if (i % 100 == 0) {
                    Log.v(ContentValues.TAG, "ParseResponse(Buch) buchId: $buchId buchTitel: $buchTitel unterTitel: $unterTitel erscheinjahr: $erscheinjahr")
                }
                entityList.add(entity)
            }
            mBlechDao.insertAllBuecher(entityList)
            entityList.removeAll(List!!)
            Util.setPreferences(Constant.PREF_AUTO_NR_BUCH, MainActivity.autoNrBuch.toString())
        } else {
            Log.d(ContentValues.TAG, "ParseResponse(Buch) ERROR Non-existent clickAction found:$clickAction")
        }
        Log.v(ContentValues.TAG, "ParseResponse(Buch) End)")
*/
    }


/*
    fun titel(restResponse: SoapObject, clickAction: String, mBlechDao: BlechDao, mTitelList: List<TitelClass>) {
        Log.d(ContentValues.TAG, "ParseResponse(Titel)")
        if (clickAction === "GetTitel2") {
            mBlechDao.deleteAllTitel()
            val entityList: MutableList<TitelClass> = ArrayList()
            val entity = TitelClass(0, "", "", "")
            val propertyCount = restResponse.propertyCount
            for (i in 0 until propertyCount) {   // whole file ends at dataset.getPropertyCount()
                val dataset = restResponse.getProperty(i) as SoapObject
                val titelKomma = dataset.getPropertyAsString("TitelKomma")
                val titel = dataset.getPropertyAsString("Titel")
                val titelIx = dataset.getPropertyAsString("Ix")
                if (i % 100 == 0) {
                    Log.v(ContentValues.TAG, "TitelKomma: " + titelKomma + "Titel: " + titel + "Ix: " + titelIx)
                }
                entity.titelOhneKomma = titelKomma
                entity.titel = titel
                entity.titelIx = titelIx
                // add to list after all
                //mBlechDao.insert(entity);
                entityList.add(entity)
            }
            mBlechDao.insertAllTitel(entityList)
            entityList.removeAll(entityList)
            Util.setPreferences(Constant.PREF_AUTO_NR_TITEL, MainActivity.autoNrTitel.toString())
            return
        } else {
            Log.d(ContentValues.TAG, "ParseResponse(Titel) ERROR Non-existent clickAction found:$clickAction")
        }
        Log.v(ContentValues.TAG, "ParseResponse(Titel) End")
        return
    }

    fun komponist(restResponse: SoapObject, clickAction: String, mBlechDao: BlechDao, mKomponistList: List<KomponistClass>) {
        Log.d(ContentValues.TAG, "ParseResponse(Komponist)")
        if (clickAction === "GetKomponisten") {
            mBlechDao.deleteAllKomponist()
            val entityList: MutableList<KomponistClass> = ArrayList()
            val entity = KomponistClass(0, "", "", "", "")
            val propertyCount = restResponse.propertyCount
            for (i in 0 until propertyCount) {   // whole file ends at dataset.getPropertyCount()
                val dataset = restResponse.getProperty(i) as SoapObject
                val komponist = dataset.getPropertyAsString("Komponist")
                val kurz = dataset.getPropertyAsString("kurz")
                val geboren = dataset.getPropertyAsString("Geboren")
                val gestorben = dataset.getPropertyAsString("Gestorben")
                if (i % 100 == 0) {
                    Log.v(ContentValues.TAG, "ParseResponse (Komponist): komponist: $komponist kurz: $kurz geboren: $geboren gestorben: $gestorben")
                }
                entity.komponist = komponist
                entity.kurz = kurz
                entity.geboren = geboren
                entity.gestorben = gestorben
                //mBlechDao.insert(entity);
                entityList.add(entity)
            }
            mBlechDao.insertAllKomponisten(entityList)
            entityList.removeAll(entityList)
            Util.setPreferences(Constant.PREF_AUTO_NR_KOMPONIST, MainActivity.autoNrKomponist.toString())
            return
        } else {
            Log.d(ContentValues.TAG, "ParseResponse(Komponist) ERROR Non-existent clickAction found:$clickAction")
        }
        Log.v(ContentValues.TAG, "ParseResponse(Komponist) End)")
        return
    }

    fun fundstellenLied(restResponse: SoapObject, clickAction: String, mList: MutableList<TitelInBuchClass>): MutableList<TitelInBuchClass> {
        Log.d(ContentValues.TAG, "ParseResponse(fundstellenLied) clickAction $clickAction")
        if (clickAction === "GetEGLiederFundstellen") {
            mList.clear()
            val propertyCount = restResponse.propertyCount
            for (i in 0 until propertyCount) {   // whole file ends at dataset.getPropertyCount()
                val dataset = restResponse.getProperty(i) as SoapObject
                val buchId = dataset.getPropertyAsString("BuchId")
                val buchTitel = dataset.getPropertyAsString("Buch")
                val buchUntertitel = dataset.getPropertyAsString("UNTERTITEL")
                val titelNr = dataset.getPropertyAsString("Nr")
                val titel = dataset.getPropertyAsString("TITEL")
                var zus = ""
                if (dataset.hasProperty("Zus")) zus = dataset.getPropertyAsString("Zus")
                var titelKomponist = ""
                if (dataset.hasProperty("Komponist")) {
                    titelKomponist = dataset.getPropertyAsString("Komponist")
                }
                val titelBesetzung = dataset.getPropertyAsString("Besetzung")
                val titelVorzeichen = dataset.getPropertyAsString("Vorzeich")

                // sometimes not existing -> to be checked
//                String herausgVorname = dataset.getPropertyAsString("herausgVorname");
//                String verlag = dataset.getPropertyAsString("VERLAG");
//                String verlagsNummer = dataset.getPropertyAsString("Verlagsnummer");
                var imgUrl = dataset.getPropertyAsString("ImgURL")
                imgUrl = imgUrl.replace("/mini60", "/mini240")
                if (i % 10 == 0) {
                    Log.v(ContentValues.TAG, "ParseResponse(fundstellenLied) buchTitel: " + buchTitel + " buchUntertitel: " + buchUntertitel + " titel: " + titel +
                            " titelBesetzung: " + titelBesetzung + " titelVorzeichen: " + titelVorzeichen)
                }
                val entity = TitelInBuchClass()
                entity.detailListItemType = TitelInBuchClass.Companion.egLiedFundstellen
                entity.buchId = buchId
                entity.buchTitel = buchTitel
                entity.buchUntertitel = buchUntertitel
                entity.imgUrl= imgUrl
                entity.titelNr = titelNr
                entity.titel = titel
                entity.zus = zus
                entity.titelKomponist = titelKomponist
                entity.titelBesetzung = titelBesetzung
                entity.titelVorzeichen = titelVorzeichen
                // add to list after all
                mList.add(entity)
            }
            return mList
        } else {
            Log.d(ContentValues.TAG, "ParseResponse(Lied) ERROR Non-existent clickAction found:$clickAction")
        }
        Log.d(ContentValues.TAG, "ParseResponse(funstellenLied) nothing to return)")
        return mList
    }

    fun fundstellenBuch(restResponse: SoapObject, clickAction: String, mList: MutableList<TitelInBuchClass>): MutableList<TitelInBuchClass> {
        Log.d(ContentValues.TAG, "ParseResponse(fundstellenBuch) clickAction:$clickAction")
        if (clickAction === "GetTitelFundstellenIx") {
            mList.clear()
            val propertyCount = restResponse.propertyCount
            for (i in 0 until propertyCount) {   // whole file ends at dataset.getPropertyCount()
                val dataset = restResponse.getProperty(i) as SoapObject
                val buchId = dataset.getPropertyAsString("BuchId")
                val buchTitel = dataset.getPropertyAsString("Buch")
                val buchUntertitel = dataset.getPropertyAsString("UNTERTITEL")
                val titelNr = dataset.getPropertyAsString("Nr")
                val titel = dataset.getPropertyAsString("TITEL")
                var zus = ""
                if (dataset.hasProperty("Zus")) zus = dataset.getPropertyAsString("Zus")
                var titelKomponist = ""
                if (dataset.hasProperty("Komponist")) {
                    titelKomponist = dataset.getPropertyAsString("Komponist")
                }
                val titelBesetzung = dataset.getPropertyAsString("Besetzung")
                val titelVorzeichen = dataset.getPropertyAsString("Vorzeich")

                // sometimes not existing -> to be checked
//                String herausgVorname = dataset.getPropertyAsString("herausgVorname");
//                String verlag = dataset.getPropertyAsString("VERLAG");
//                String verlagsNummer = dataset.getPropertyAsString("Verlagsnummer");
                var imgUrl = dataset.getPropertyAsString("ImgURL")
                imgUrl = imgUrl.replace("/mini60", "/mini240")
                if (i % 10 == 0) {
                    Log.v(ContentValues.TAG, "ParseResponse(fundstellenBuch) buchTitel: " + buchTitel + " buchUntertitel: " + buchUntertitel + " erscheinjahr: " + titel +
                            " titelBesetzung: " + titelBesetzung + " titelVorzeichen: " + titelVorzeichen)
                }
                val entity = TitelInBuchClass()
                entity.detailListItemType = TitelInBuchClass.Companion.titelFundstellen
                entity.buchId = buchId
                entity.buchTitel = buchTitel
                entity.buchUntertitel = buchUntertitel
                entity.imgUrl = imgUrl
                entity.titelNr = titelNr
                entity.titel = titel
                entity.zus = zus
                entity.titelKomponist = titelKomponist
                entity.titelBesetzung = titelBesetzung
                entity.titelVorzeichen = titelVorzeichen
                // add to list after all
                mList.add(entity)
            }
            return mList
        } else {
            Log.d(ContentValues.TAG, "ParseResponse(fundstellenBuch) ERROR Non-existent clickAction found:$clickAction")
        }
        Log.d(ContentValues.TAG, "ParseResponse(fundstellenBuch) nothing to return")
        return mList
    }

    fun fundstellenTitel(restResponse: SoapObject, clickAction: String, mList: MutableList<TitelInBuchClass>): MutableList<TitelInBuchClass> {
        Log.d(ContentValues.TAG, "ParseResponse(fundstellenTitel) clickAction:$clickAction")
        if (clickAction === "GetBuchFundstellen") {
            mList.clear()
            val propertyCount = restResponse.propertyCount
            for (i in 0 until propertyCount) {   // whole file ends at dataset.getPropertyCount()
                val dataset = restResponse.getProperty(i) as SoapObject
                val buchId = dataset.getPropertyAsString("BuchId")
                val buchTitel = dataset.getPropertyAsString("Buch")
                val buchUntertitel = dataset.getPropertyAsString("UNTERTITEL")
                val titelNr = dataset.getPropertyAsString("Nr")
                val titel = dataset.getPropertyAsString("TITEL")
                var zus = ""
                if (dataset.hasProperty("Zus")) zus = dataset.getPropertyAsString("Zus")
                var titelKomponist = ""
                if (dataset.hasProperty("Komponist")) {
                    titelKomponist = dataset.getPropertyAsString("Komponist")
                }
                val titelBesetzung = dataset.getPropertyAsString("Besetzung")
                val titelVorzeichen = dataset.getPropertyAsString("Vorzeich")
                var audioURL = ""
                if (dataset.hasProperty("AudioURL")) {
                    audioURL = dataset.getPropertyAsString("AudioURL")
                }

                // sometimes not existing -> to be checked
//                String herausgVorname = dataset.getPropertyAsString("herausgVorname");
//                String verlag = dataset.getPropertyAsString("VERLAG");
//                String verlagsNummer = dataset.getPropertyAsString("Verlagsnummer");
                var imgUrl = dataset.getPropertyAsString("ImgURL")
                imgUrl = imgUrl.replace("/mini60", "/mini240")
                if (i % 10 == 0) {
                    Log.v(ContentValues.TAG, "ParseResponse(fundstellenTitel) buchTitel: $buchTitel, buchUntertitel: $buchUntertitel, titel: $titel, imgUrl:$imgUrl")
                }
                val entity = TitelInBuchClass()
                // buchFundstellen to be added to DetailListItemType, FundstellenTitelFragment and TitelInBuchAdapter as static variable
                entity.detailListItemType = TitelInBuchClass.Companion.buchFundstellen
                entity.buchId = buchId
                entity.buchTitel = buchTitel
                entity.buchUntertitel = buchUntertitel
                entity.imgUrl = imgUrl
                entity.titelNr = titelNr
                entity.titel = titel
                entity.zus = zus
                entity.titelKomponist = titelKomponist
                entity.titelBesetzung = titelBesetzung
                entity.titelVorzeichen = titelVorzeichen
                entity.audioURL = audioURL

                // add to list after all
                mList.add(entity)
            }
            Log.d(ContentValues.TAG, "ParseResponse(fundstellenTitel) End: try return??")
            return mList
        } else if (clickAction === "GetKomponistFundstellen") {
            mList.clear()
            val propertyCount = restResponse.propertyCount
            for (i in 0 until propertyCount) {   // whole file ends at dataset.getPropertyCount()
                val dataset = restResponse.getProperty(i) as SoapObject
                val buchId = dataset.getPropertyAsString("BuchId")
                val buchTitel = dataset.getPropertyAsString("Buch")
                val buchUntertitel = dataset.getPropertyAsString("UNTERTITEL")
                val titelNr = dataset.getPropertyAsString("Nr")
                val titel = dataset.getPropertyAsString("TITEL")
                var zus = ""
                if (dataset.hasProperty("Zus")) zus = dataset.getPropertyAsString("Zus")
                var titelKomponist = ""
                if (dataset.hasProperty("Komponist")) {
                    titelKomponist = dataset.getPropertyAsString("Komponist")
                }
                val titelBesetzung = dataset.getPropertyAsString("Besetzung")
                val titelVorzeichen = dataset.getPropertyAsString("Vorzeich")

                // sometimes field not existing -> to be checked and excluded
//                String herausgVorname = dataset.getPropertyAsString("herausgVorname");
//                String verlag = dataset.getPropertyAsString("VERLAG");
//                String verlagsNummer = dataset.getPropertyAsString("Verlagsnummer");
                var imgUrl = dataset.getPropertyAsString("ImgURL")
                imgUrl = imgUrl.replace("/mini60", "/mini240")
                if (i % 10 == 0) {
                    Log.v(ContentValues.TAG, "ParseResponse(fundstellenTitel) buchTitel: $buchTitel buchUntertitel: $buchUntertitel titel: $titel imgUrl:$imgUrl titelNr:$titelNr titelVorzeichen:$titelVorzeichen titelBesetzung:$titelBesetzung")
                }
                val entity = TitelInBuchClass()
                entity.detailListItemType = TitelInBuchClass.Companion.komponistFundstellen
                entity.buchId = buchId
                entity.buchTitel = buchTitel
                entity.buchUntertitel = buchUntertitel
                entity.imgUrl = imgUrl
                entity.titelNr = titelNr
                entity.titel = titel
                entity.zus = zus
                entity.titelKomponist = titelKomponist
                entity.titelBesetzung = titelBesetzung
                entity.titelVorzeichen = titelVorzeichen
                // add to list after all
                mList.add(entity)
            }
            return mList
        } else {
            Log.d(ContentValues.TAG, "ParseResponse(fundstellenTitel) ERROR Non-existent clickAction found:$clickAction")
        }
        Log.d(ContentValues.TAG, "ParseResponse(fundstellenTitel) nothing to return")
        return mList
    }

    fun lastAutoNr(restResponse: SoapObject, clickAction: String, mBlechDao: BlechDao?, mList: MutableList<AutoNrClass>): MutableList<AutoNrClass> {
        Log.d(ContentValues.TAG, "ParseResponse(lastAutoNr)")
        mList?.clear()
        if (clickAction === "GetlastAutonummer") {
            val propertyCount = restResponse.propertyCount
            for (i in 0 until propertyCount) {   // whole file ends at dataset.getPropertyCount()
                val dataset = restResponse.getProperty(i) as SoapObject
                val tbl = dataset.getPropertyAsString("tbl")
                val lastNr = dataset.getPropertyAsString("lastNr")
                Log.v(ContentValues.TAG, "ParseResponse(lastAutoNr) $tbl=$lastNr")
                val entity = AutoNrClass()
                entity.tbl = tbl
                entity.lastNr = lastNr
                // add to list after all
                mList?.add(entity)
            }
            return mList
        } else {
            Log.d(ContentValues.TAG, "ParseResponse(lastAutoNr) ERROR Non-existent clickAction found:$clickAction")
        }
        Log.v(ContentValues.TAG, "ParseResponse(lastAutoNr) End")
        return mList
    }
*/
}


