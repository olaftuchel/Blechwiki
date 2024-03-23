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
    // name in SQL table: Constant.BUCHID, buchId: name in JSON response
    @ColumnInfo(name = Constant.BUCHID) var buchId: String,
    @ColumnInfo(name = Constant.BUCHKURZ) var buchkurz: String,
    @ColumnInfo(name = Constant.BUCH) var buch: String,
    @ColumnInfo(name = Constant.UNTERTITEL) var untertitel: String?,
    @ColumnInfo(name = Constant.ERSCHEINJAHR) var erscheinjahr: String?,
    @ColumnInfo(name = Constant.HERAUSGEBER) var herausgeber: String?,
    @ColumnInfo(name = Constant.HERAUSG_VORNAME) var herausg_vorname: String?,
    @ColumnInfo(name = Constant.VERLAG) var verlag: String?,
    @ColumnInfo(name = Constant.VERLAGSNUMMER) var verlagsnummer: String?,
    @ColumnInfo(name = Constant.ZULIEFERUNG) var zulieferung: String?,
    @ColumnInfo(name = Constant.RELEVANZ) var relevanz: Int,
    @ColumnInfo(name = Constant.VORHANDEN) var vorhanden: String,
//     @ColumnInfo(name = Constant.IMGURL) var imgUrl: String,
    @ColumnInfo(name = Constant.CHANGECOUNTER) var changecounter: Int,
    @ColumnInfo(name = Constant.CHANGE) var change: String
    )

