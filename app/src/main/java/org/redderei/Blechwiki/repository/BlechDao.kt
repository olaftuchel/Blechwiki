package org.redderei.Blechwiki.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import org.redderei.Blechwiki.gettersetter.LiedClass
import org.redderei.Blechwiki.gettersetter.BuchClass
import org.redderei.Blechwiki.gettersetter.KomponistClass
import org.redderei.Blechwiki.gettersetter.TitelClass

/**
 * The Room Magic is in this file, where you map a Java method call to an SQL query.
 *
 * When you are using complex data types, such as Date, you have to also supply type converters.
 * To keep this example basic, no types that require type converters are used.
 * See the documentation at
 * https://developer.android.com/topic/libraries/architecture/room.html#type-converters
 */
@Dao
interface BlechDao {
    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    // table names and variables are also noted in Constant.java class
    // COLLATE LOCALIZED or COLLATE NOCASE, not both at a time

    // Lieder
    @Query("SELECT * FROM lied_table WHERE ((teil = 'Stamm' OR teil = :kirche) AND lied LIKE '%' || :query || '%') ORDER BY lied COLLATE LOCALIZED ASC")
    fun getAllLiedSortABC(kirche: String, query: String): LiveData<List<LiedClass>>

    @Query("SELECT * FROM lied_table WHERE ((teil = 'Stamm' OR teil = :kirche) AND lied LIKE '%' || :query || '%') ORDER BY CAST (nr AS INTEGER)")
    fun getAllLiedSortNr(kirche: String, query: String): LiveData<List<LiedClass>>

    @Query("SELECT * FROM lied_table WHERE ((teil = 'Stamm' OR teil = :kirche) AND lied LIKE '%' || :query || '%') ORDER BY anlass ASC")
    fun getAllLiedSortAnlass(kirche: String, query: String): LiveData<List<LiedClass>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(lied: LiedClass)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllLied(lied: List<LiedClass>)

    @Delete
    fun delete(lied: LiedClass)

    // Buecher
    @Query("SELECT * FROM buch_table WHERE (buch LIKE '%' || :query || '%') ORDER BY buch COLLATE LOCALIZED ASC")
    fun getAllBuch(query: String?): LiveData<List<BuchClass>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBuch(buch: List<BuchClass>)

    @Delete()
    suspend fun deleteBuch(buch: List<BuchClass>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBuch(buch: List<BuchClass>)

    // Komponisten
    @Query("SELECT * FROM komponist_table WHERE (komponist LIKE '%' || :query || '%') ORDER BY komponist COLLATE LOCALIZED ASC")
    fun getAllKomponist(query: String?): LiveData<List<KomponistClass>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertKomponist(komponist: List<KomponistClass>)

    @Delete()
    fun deleteKomponist(komponist: List<KomponistClass>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateKomponist(komponist: List<KomponistClass>)

    // Titel
    @Query("SELECT * FROM titel_table WHERE (titel LIKE '%' || :query || '%') ORDER BY titel COLLATE LOCALIZED ASC")
    fun getAllTitel(query: String): LiveData<List<TitelClass>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTitel(titel: List<TitelClass>)

    @Delete()
    fun deleteTitel(titel: List<TitelClass>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTitel(titel: List<TitelClass>)

    @Query("SELECT * FROM titel_table WHERE (titel LIKE '%' || :query || '%' OR titel_ohne_komma LIKE '%' || :query || '%') " +
                "ORDER BY titel COLLATE LOCALIZED ASC")
    fun getAllTitelKomma(query: String): LiveData<List<TitelClass>>
}
