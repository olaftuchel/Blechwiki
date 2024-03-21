package org.redderei.Blechwiki.gettersetter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// https://www.programmierenlernenhq.de/sqlite-datenbank-in-android-app-integrieren/ original name shoppingMemo
/**
 * Created by ot775x on 27.01.2018.
 */
@Entity(tableName = Constant.TABLE_LIED)
class LiedClass(// "Abend ward, bald kommt die Nacht"
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = Constant.LIED) var lied: String, // "2"
    @ColumnInfo(name = Constant.IX) var ix: String, // "2"
    @ColumnInfo(name = Constant.IXUR) var ixUr: String, // "Stamm"
    @ColumnInfo(name = Constant.TEIL) var teil: String, // "487"
    @ColumnInfo(name = Constant.NR) var nr: String, // "Abend"
    @ColumnInfo(name = Constant.ANLASS) var anlass: String
    )