package org.redderei.Blechwiki.gettersetter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by ot775x on 25.02.2018, changed to RestAPI 23.10.2022
 */

@Entity(tableName = Constant.TABLE_BUCH)
class BuchClass(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = Constant.BUCHID) var BuchId: String,
    @ColumnInfo(name = Constant.BUCHKURZ) var Buchkurz: String,
    @ColumnInfo(name = Constant.BUCH) var Buch: String,
    @ColumnInfo(name = Constant.UNTERTITEL) var Untertitel: String,
    @ColumnInfo(name = Constant.ERSCHEINJAHR) var Erscheinjahr: String,
    @ColumnInfo(name = Constant.HERAUSGEBER) var Herausgeber: String,
    @ColumnInfo(name = Constant.HERAUSG_VORNAME) var Herausg_vorname: String,
    @ColumnInfo(name = Constant.VERLAG) var Verlag: String,
    @ColumnInfo(name = Constant.VERLAGSNUMMER) var Verlagsnummer: String,
    @ColumnInfo(name = Constant.ZULIEFERUNG) var Zulieferung: String,
    @ColumnInfo(name = Constant.RELEVANZ) var Relevanz: String,
    @ColumnInfo(name = Constant.VORHANDEN) var vorhanden: String,
//     @ColumnInfo(name = Constant.IMGURL) var imgUrl: String,
    @ColumnInfo(name = Constant.CHANGECOUNTER) var changecounter: String,
    @ColumnInfo(name = Constant.CHANGE) var change: String
    )

