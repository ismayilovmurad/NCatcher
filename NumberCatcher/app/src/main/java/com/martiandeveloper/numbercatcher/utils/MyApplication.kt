@file:Suppress("unused")

package com.martiandeveloper.numbercatcher.utils

import android.app.Application
import timber.log.Timber

const val IN_APP_UPDATE_REQUEST_CODE = 100

const val SCORE_SHARED_PREFERENCES = "Score"
const val SCORE_KEY = "score"

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

}
