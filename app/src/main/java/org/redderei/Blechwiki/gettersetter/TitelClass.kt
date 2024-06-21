package org.redderei.Blechwiki.gettersetter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by ot775x on 25.02.2018, changed to RestAPI 23.10.2022
 */
@Entity(tableName = Constant.TABLE_TITEL)
class TitelClass(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = Constant.TITELOHNEKOMMA) val titelohneKomma: String,
    @ColumnInfo(name = Constant.TITEL) val titel: String,
    @ColumnInfo(name = Constant.IX) val ix: String,
    @ColumnInfo(name = Constant.CHANGECOUNTER) var changecounter: Int,
    @ColumnInfo(name = Constant.CHANGE) var change: String
    )