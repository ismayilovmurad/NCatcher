package com.martiandeveloper.numbercatcher.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.martiandeveloper.numbercatcher.R
import com.martiandeveloper.numbercatcher.utils.IN_APP_UPDATE_REQUEST_CODE
import com.martiandeveloper.numbercatcher.utils.SCORE_KEY
import com.martiandeveloper.numbercatcher.utils.SCORE_SHARED_PREFERENCES
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var appUpdateManager: AppUpdateManager? = null

    private var reviewInfo: ReviewInfo? = null
    private lateinit var reviewManager: ReviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        window.setBackgroundDrawableResource(R.drawable.background)
        setContentView(R.layout.activity_main)

        checkUpdate()

        setAds()

        initReview()

        if (getBestScore() > 0) {
            showRateUs()
        }
    }

    private fun checkUpdate() {

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager?.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    IN_APP_UPDATE_REQUEST_CODE
                )
            }
        }

    }

    override fun onResume() {
        super.onResume()

        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                appUpdateManager!!.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    IN_APP_UPDATE_REQUEST_CODE
                )
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == IN_APP_UPDATE_REQUEST_CODE) {

            when (resultCode) {

                Activity.RESULT_OK -> {
                    Timber.i("The result is okay")
                }

                Activity.RESULT_CANCELED -> {
                    Timber.i("The result is cancelled")
                }

                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Timber.i("The result In-app update failed")
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun setAds() {
        MobileAds.initialize(this)

        val bannerAdRequest = AdRequest.Builder().build()
        activity_main_mainAV.loadAd(bannerAdRequest)
    }

    private fun initReview() {

        reviewManager = ReviewManagerFactory.create(this)

        val requestFlow = reviewManager.requestReviewFlow()
        requestFlow.addOnCompleteListener { request ->
            reviewInfo = if (request.isSuccessful) {
                request.result
            } else {
                null
            }
        }

    }

    private fun getBestScore(): Int {

        val sharedPreferences: SharedPreferences = getSharedPreferences(
            SCORE_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )

        return sharedPreferences.getInt(SCORE_KEY, 0)

    }

    private fun showRateUs() {

        reviewInfo?.let {
            val flow = reviewManager.launchReviewFlow(this, it)
            flow.addOnSuccessListener {}
            flow.addOnFailureListener {}
            flow.addOnCompleteListener {}
        }

    }

}
