package org.redderei.Blechwiki.repository

import org.redderei.Blechwiki.gettersetter.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RestInterface {
    // Use the @GET annotation to tell Retrofit that this is GET request, and specify an endpoint, for that web service method
    @GET("Version")
    fun getAutoNr () : Call<AutoNrClass>


    @GET("EG")
    fun getLiedList () : Call<List<LiedClass>>

    @GET("Version")
    fun getBuchList(   // query:
        @Query("Tabelle") type: String,
        @Query("counter") count: String) : Call<List<BuchClass>>

    @GET("Version")
    fun getKomponistList(   // query: xxx
        @Query("Tabelle") type: String,
        @Query("counter") count: String) : Call<List<KomponistClass>>

    @GET("Version")
    fun getTitelList(   // query: xxx
        @Query("Tabelle") type: String,
        @Query("counter") count: String) : Call<List<TitelClass>>

    @GET("EG/{id}")
    fun getLiedDetails(
        @Path("id") id: Int): Call<List<TitelInBuchClass>>

    @GET("Buch/{id}")
    fun getBuchDetails(
        @Path("id") id: Int): Call<List<TitelInBuchClass>>

    @GET("Komponist/{id}")
    fun getKomponistDetails(
        @Path("id") id: Int): Call<List<TitelInBuchClass>>

    @GET("Titel/{id}")
    fun getTitelDetails(
        @Path("id") id: Int): Call<List<TitelInBuchClass>>
}



