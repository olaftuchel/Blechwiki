package org.redderei.Blechwiki.repository

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import org.redderei.Blechwiki.gettersetter.LiedClass
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass

/**
 xxx ViewModel:
  - Fragments retrieve all data from here
  - LiveData instantly shows up data changes on the UI
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

class LiedViewModel(app: Application) : AndroidViewModel(app) {
    private val mRepository: LiedRepository

    suspend fun getAllLieder(mKirche: String, sortType: String, query: String): LiveData<List<LiedClass>>? {
        Log.d("LiedViewModel", "getAllLieder")
        return mRepository.getAllLieder(mKirche, sortType, query)
    }

    fun getLiedDetails(liedNr: Int): MutableLiveData<List<TitelInBuchClass>> {
        Log.v("LiedViewModel", "getLiedDetails " + liedNr);
        return mRepository.getLiedDetails(liedNr);
    }

    init {
        Log.d("LiedViewModel", "init")
        mRepository = LiedRepository(app)
    }
}
