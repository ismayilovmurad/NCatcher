package com.martiandeveloper.numbercatcher.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.martiandeveloper.numbercatcher.R
import com.martiandeveloper.numbercatcher.databinding.DialogGameOverBinding
import com.martiandeveloper.numbercatcher.databinding.DialogPauseBinding
import com.martiandeveloper.numbercatcher.databinding.FragmentGameBinding
import com.martiandeveloper.numbercatcher.utils.SCORE_KEY
import com.martiandeveloper.numbercatcher.utils.SCORE_SHARED_PREFERENCES
import com.martiandeveloper.numbercatcher.viewmodel.GameViewModel
import timber.log.Timber

class GameFragment : Fragment() {

    private lateinit var fragmentGameBinding: FragmentGameBinding

    private lateinit var gameViewModel: GameViewModel

    private lateinit var musicMediaPlayer: MediaPlayer

    private lateinit var gameOverDialog: AlertDialog

    private lateinit var pauseDialog: AlertDialog

    private lateinit var interstitialAd: InterstitialAd

    private lateinit var clickMediaPlayer: MediaPlayer

    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        return fragmentGameBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        gameViewModel = getViewModel()

        fragmentGameBinding.gameViewModel = gameViewModel
        fragmentGameBinding.lifecycleOwner = this

        musicMediaPlayer = MediaPlayer.create(context, R.raw.music)

        gameOverDialog = AlertDialog.Builder(context).create()
        pauseDialog = AlertDialog.Builder(context).create()

        getBestScore()

        setMusic()

        observe()

        interstitialAd = InterstitialAd(context)

        setAds()

        clickMediaPlayer = MediaPlayer.create(context, R.raw.click)

        setClick()

        startTimer()
    }

    private fun getViewModel(): GameViewModel {

        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel() as T
            }
        })[GameViewModel::class.java]

    }

    private fun getBestScore() {

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(
            SCORE_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        gameViewModel.setBestScore(sharedPreferences.getInt(SCORE_KEY, 0))

    }

    private fun setMusic() {

        try {
            musicMediaPlayer.isLooping = true
            musicMediaPlayer.prepare()
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
        }

    }

    private fun observe() {
        gameViewModel.eventContinueMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                musicMediaPlayer.seekTo(gameViewModel.currentPosition.value!!)
                musicMediaPlayer.start()

                pauseDialog.dismiss()

                startTimer()

                gameViewModel.onContinueMBTNClickComplete()
            }
        })

        gameViewModel.eventHomeMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                pauseDialog.dismiss()

                navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())

                gameViewModel.onHomeMBTNClickComplete()
            }
        })

        gameViewModel.eventFirstNumberMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                handleNumberClick(0)

                gameViewModel.onFirstNumberMBTNClickComplete()
            }
        })

        gameViewModel.eventSecondNumberMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                handleNumberClick(1)

                gameViewModel.onSecondNumberMBTNClickComplete()
            }
        })

        gameViewModel.eventThirdNumberMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                handleNumberClick(2)

                gameViewModel.onThirdNumberMBTNClickComplete()
            }
        })

        gameViewModel.eventFourthNumberMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                handleNumberClick(3)

                gameViewModel.onFourthNumberMBTNClickComplete()
            }
        })

        gameViewModel.eventTryAgainMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                gameOverDialog.dismiss()

                navigate(GameFragmentDirections.actionGameFragmentSelf())

                gameViewModel.onTryAgainMBTNClickComplete()

                if (interstitialAd.isLoaded) {
                    interstitialAd.show()
                }
            }
        })

        gameViewModel.eventHome2MBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                gameOverDialog.dismiss()

                navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())

                gameViewModel.onHome2MBTNClickComplete()
            }
        })
    }

    private fun navigate(navDirections: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(navDirections) }
    }

    private fun handleNumberClick(index: Int) {
        clickMediaPlayer.start()

        if (gameViewModel.numbers.value!![index] == gameViewModel.catchableNumber.value) {
            gameViewModel.increaseScore()

            gameViewModel.generateCatchableNumber()
            gameViewModel.generateNumbers()

            countDownTimer.cancel()

            gameViewModel.setProgress(3000)

            startTimer()
        } else {
            countDownTimer.cancel()

            if (gameViewModel.score.value!! > gameViewModel.bestScore.value!!) {
                saveScore()
            }

            showGameOverDialog()

            musicMediaPlayer.pause()
        }
    }

    private fun saveScore() {
        val sharedPreferences = requireContext().getSharedPreferences(
            SCORE_SHARED_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(SCORE_KEY, gameViewModel.score.value!!)
        editor.apply()
    }

    override fun onPause() {
        super.onPause()

        musicMediaPlayer.pause()

        gameViewModel.setCurrentPosition(musicMediaPlayer.currentPosition)

        countDownTimer.cancel()

        gameViewModel.setProgress(3000)
    }

    override fun onResume() {
        super.onResume()

        if (gameViewModel.currentPosition.value == null) {
            musicMediaPlayer.start()
        } else {
            if (!pauseDialog.isShowing && !gameOverDialog.isShowing) {
                countDownTimer.cancel()

                showPauseDialog()
            }
        }
    }

    private fun showPauseDialog() {
        val binding = DialogPauseBinding.inflate(LayoutInflater.from(context))

        binding.gameViewModel = gameViewModel
        binding.lifecycleOwner = this

        pauseDialog.setView(binding.root)
        pauseDialog.setCanceledOnTouchOutside(false)
        pauseDialog.setCancelable(false)
        pauseDialog.show()
    }

    private fun showGameOverDialog() {
        val binding = DialogGameOverBinding.inflate(LayoutInflater.from(context))

        binding.gameViewModel = gameViewModel
        binding.lifecycleOwner = this

        gameOverDialog.setView(binding.root)
        gameOverDialog.setCanceledOnTouchOutside(false)
        gameOverDialog.setCancelable(false)
        gameOverDialog.show()
    }

    private fun setAds() {
        interstitialAd.adUnitId = getString(R.string.main_interstitial)

        val interstitialAdRequest = AdRequest.Builder().build()
        interstitialAd.loadAd(interstitialAdRequest)

        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                interstitialAd.loadAd(interstitialAdRequest)
            }
        }

    }

    private fun setClick() {

        try {
            clickMediaPlayer.prepare()
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
        }

    }

    private fun startTimer() {

        countDownTimer = object : CountDownTimer(3000, 1) {

            override fun onTick(leftTimeInMilliseconds: Long) {
                gameViewModel.setProgress(leftTimeInMilliseconds.toInt())
            }

            override fun onFinish() {
                countDownTimer.cancel()

                if (gameViewModel.score.value!! > gameViewModel.bestScore.value!!) {
                    saveScore()
                }

                showGameOverDialog()

                musicMediaPlayer.pause()
            }

        }.start()

    }

}
