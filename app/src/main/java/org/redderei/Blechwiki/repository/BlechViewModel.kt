package org.redderei.Blechwiki.repository

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import org.redderei.Blechwiki.gettersetter.BuchClass
import org.redderei.Blechwiki.gettersetter.KomponistClass
import org.redderei.Blechwiki.gettersetter.TitelClass

/**
 xxx ViewModel:
  - Fragments retrieve all data from here
  - retrieve data from repository
  - general concepts:
  https://medium.com/androiddevelopers/viewmodels-a-simple-example-ed5ac416317e
  https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-6-working-with-architecture-components/lesson-14-room,-livedata,-viewmodel/14-1-a-room-livedata-viewmodel/14-1-a-room-livedata-viewmodel.html#task2intro
  https://developer.android.com/codelabs/basic-android-kotlin-training-getting-data-internet#0


  // LiveData is a data holder class that can be observed within a given lifecycle. This means
  // that an Observer can be added in a pair with a LifecycleOwner, and this observer will be
  // notified about modifications of the wrapped data only if the paired LifecycleOwner is in active state.
  // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
  // - We can put an observer on the data (instead of polling for changes) and only update the
  //   the UI when the data actually changes.
  // - Repository is completely separated from the UI through the ViewModel.
*/

class BlechViewModel(app: Application) : AndroidViewModel(app) {
    private val mRepository: BlechRepository

    suspend fun getAllBuecher(query: String): LiveData<List<BuchClass>>? {
        Log.d(ContentValues.TAG, "BlechViewModel (getAllBuecher)")
//        GlobalScope.launch{ mRepository.getAllBuecher(query)}
//        return withContext(Dispatchers.IO) {mRepository.getAllBuecher(query)}
        return mRepository.getAllBuch(query)
    }

    fun getAllKomponist(query: String): LiveData<List<KomponistClass>>? {
        Log.d(ContentValues.TAG, "BlechViewModel (getAllKomponist)")
        return mRepository.getAllKomponist(query)
    }

    fun getAllTitel(query: String): LiveData<List<TitelClass>>? {
        Log.d(ContentValues.TAG, "BlechViewModel (getAllTitel)")
        return mRepository.getAllTitel(query)
    }

    fun getAllTitelKomma(query: String): LiveData<List<TitelClass>>? {
        Log.d(ContentValues.TAG, "BlechViewModel (getAllTitel)")
        return mRepository.getAllTitelKomma(query)
    }

    init {
        Log.d(ContentValues.TAG, "BlechViewModel (init)")
        mRepository = BlechRepository(app)
    }
}
