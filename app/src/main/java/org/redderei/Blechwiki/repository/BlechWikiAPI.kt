package org.redderei.Blechwiki.repository

import org.redderei.Blechwiki.gettersetter.AutoNrClass
import org.redderei.Blechwiki.gettersetter.BuchClass
import org.redderei.Blechwiki.gettersetter.KomponistClass
import org.redderei.Blechwiki.gettersetter.TitelClass
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.GET

class BlechRemoteDataSource(
    private val api: BlechWikiAPI.RestGetBuchList
) {
//    suspend operator fun invoke(): BlechWikiAPI.NetworkResult<List<T>> =
//        handleApi { api.getBuchList() }

}

class BlechWikiAPI {

    interface RestGetAutoNr {
        // Use the @GET annotation to tell Retrofit that this is GET request, and specify an endpoint, for that web service method
        @GET("Version")
        fun getAutoNr () : Call<AutoNrClass>
    }

    interface RestGetBuchList {
        @GET("Version?Tabelle=Buch&counter=0")
        fun getBuchList () : Call<List<BuchClass>>
    }

    interface RestGetTitelList {
        @GET("Version?Tabelle=Titel&counter=0")
        fun getTitelList () : Call<List<TitelClass>>
    }

    interface RestGetKomponistList {
        @GET("Version?Tabelle=Komponist&counter=0")
        fun getKomponistList () : Call<List<KomponistClass>>
    }



    suspend fun <T : Any> handleApi(
        execute: suspend () -> Response<T>
    ): NetworkResult<T> {
        return try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                NetworkResult.ApiSuccess(body)
            } else {
                NetworkResult.ApiError(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            NetworkResult.ApiError(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            NetworkResult.ApiException(e)
        }
    }

    sealed class NetworkResult<T : Any> {
        class ApiSuccess<T: Any>(val data: T) : NetworkResult<T>()
        class ApiError<T: Any>(val code: Int, val message: String?) : NetworkResult<T>()
        class ApiException<T: Any>(val e: Throwable) : NetworkResult<T>()
    }

}