package org.redderei.Blechwiki.repository

import org.redderei.Blechwiki.gettersetter.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RestGetAutoNr {
    // Use the @GET annotation to tell Retrofit that this is GET request, and specify an endpoint, for that web service method
    @GET("Version")
    fun getAutoNr () : Call<AutoNrClass>
}

interface RestGetBuchList {
    @GET("Version?Tabelle=Buch&counter=0")
    fun getBuchList () : Call<List<BuchClass>>
}

interface getChangeBuch {
    @GET("Version?Tabelle=Buch&counter=0")
    fun getChangeBuch (changeCounter: Int) : Call<List<BuchClass>>
}

interface RestGetTitelList {
    @GET("Version?Tabelle=Titel&counter=0")
    fun getTitelList () : Call<List<TitelClass>>
}

interface RestGetKomponistList {
    @GET("Version?Tabelle=Komponist&counter= '%' || :number || '%'")
    fun getKomponistList (number: String) : Call<List<KomponistClass>>
}

interface GetRestBlechwiki {
    @GET("Version")
    fun search (   // query: Buch&counter=xxx
        @Query("Tabelle") query: String) : Call<List<BuchClass>>
}
