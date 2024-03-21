package org.redderei.Blechwiki.repository

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*

/**
 * BlechViewModel: care to retrieve all data
 */// https://medium.com/androiddevelopers/viewmodels-a-simple-example-ed5ac416317e
// https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-6-working-with-architecture-components/lesson-14-room,-livedata,-viewmodel/14-1-a-room-livedata-viewmodel/14-1-a-room-livedata-viewmodel.html#task2intro
// https://developer.android.com/codelabs/basic-android-kotlin-training-getting-data-internet#0
class AutoNrViewModel(app: Application) : AndroidViewModel(app) {
    private val mRepository: AutoNrRepository

    val getAutoNr: Unit
        get() {
            Log.d(ContentValues.TAG, "AutoNrViewModel (getGetAutoNr)")
            mRepository.getAutoNr
            return
        }

    val getLastNr: Unit
        get() {
            Log.d(ContentValues.TAG, "AutoNrViewModel (getLastNr)")
            mRepository.getLastNr
            return
        }

    // LiveData is a data holder class that can be observed within a given lifecycle. This means
    // that an Observer can be added in a pair with a LifecycleOwner, and this observer will be
    // notified about modifications of the wrapped data only if the paired LifecycleOwner is in active state.
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    init {
        Log.d(ContentValues.TAG, "AutoNrViewModel (init)")
        /*if (mRepository?.tag == "mReository") {
            Log.d (ContentValues.TAG, "mRepository is: $mRepository")
        }
        when (mRepository) {
            is Int -> Log.d (ContentValues.TAG, "mRepository is Int: $mRepository")
            is String -> Log.d (ContentValues.TAG, "mRepository is String : $mRepository.length")
            is IntArray -> Log.d (ContentValues.TAG, "mRepository is : $mRepository.sum()")
        }
         */
        mRepository = AutoNrRepository(app)
    }

}