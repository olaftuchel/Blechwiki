package org.redderei.Blechwiki.gettersetter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by ot775x on 25.02.2018, changed to RestAPI 23.10.2022
 */
@Entity(tableName = Constant.TABLE_KOMPONIST)
class KomponistClass(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = Constant.ID) val Id: Long,
    @ColumnInfo(name = Constant.KURZ) var kurz: String,
    @ColumnInfo(name = Constant.KOMPONIST) var Komponist: String,
    @ColumnInfo(name = Constant.FRIENDLYKOMPONISTNAME) var FriendlyKomponistName: String,
    @ColumnInfo(name = Constant.CHANGECOUNTER) var changecounter: Int,
    @ColumnInfo(name = Constant.CHANGE) var change: String
    )