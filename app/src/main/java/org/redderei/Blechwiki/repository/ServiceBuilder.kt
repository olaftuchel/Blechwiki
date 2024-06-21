package org.redderei.Blechwiki.repository
// https://dev.to/bensalcie/android-kotlin-get-data-from-restful-api-having-multiple-json-objects-o5a
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.redderei.Blechwiki.gettersetter.Constant
//import retrofit2.converter.scalars.ScalarsConverterFactory

object ServiceBuilder {

    // create retrofit builder
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constant.restURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }

}