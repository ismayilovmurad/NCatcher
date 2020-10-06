package com.martiandeveloper.numbercatcher.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
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
import com.martiandeveloper.numbercatcher.R
import com.martiandeveloper.numbercatcher.databinding.DialogGameOverBinding
import com.martiandeveloper.numbercatcher.databinding.DialogPauseBinding
import com.martiandeveloper.numbercatcher.databinding.FragmentGameBinding
import com.martiandeveloper.numbercatcher.utils.SCORE_KEY
import com.martiandeveloper.numbercatcher.utils.SCORE_SHARED_PREFERENCES
import com.martiandeveloper.numbercatcher.viewmodel.GameViewModel
import timber.log.Timber

class GameFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var pauseDialog: AlertDialog

    private lateinit var mainBinding: FragmentGameBinding

    private lateinit var gameViewModel: GameViewModel

    private lateinit var gameOverDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        gameViewModel = getViewModel()
        mainBinding.gameViewModel = gameViewModel
        mainBinding.lifecycleOwner = this
        mediaPlayer = MediaPlayer.create(context, R.raw.music)
        setMusic()
        observe()
        getBestScore()
        pauseDialog = AlertDialog.Builder(context).create()
        gameOverDialog = AlertDialog.Builder(context).create()
    }

    private fun getViewModel(): GameViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel() as T
            }
        })[GameViewModel::class.java]
    }

    private fun setMusic() {
        try {
            mediaPlayer.isLooping = true
            mediaPlayer.prepare()
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
        }
    }

    private fun observe() {
        gameViewModel.eventContinueMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                mediaPlayer.seekTo(gameViewModel.currentPosition.value!!)
                mediaPlayer.start()

                pauseDialog.dismiss()
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
                if (gameViewModel.numbers.value!![0] == gameViewModel.catchableNumber.value) {
                    gameViewModel.increaseScore()

                    gameViewModel.generateCatchableNumber()
                    gameViewModel.generateNumbers()
                } else {
                    if (gameViewModel.score.value!! > gameViewModel.bestScore.value!!) {
                        saveScore()
                    }

                    showGameOverDialog()

                    mediaPlayer.pause()
                }

                gameViewModel.onFirstNumberMBTNClickComplete()
            }
        })

        gameViewModel.eventSecondNumberMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                if (gameViewModel.numbers.value!![1] == gameViewModel.catchableNumber.value) {
                    gameViewModel.increaseScore()

                    gameViewModel.generateCatchableNumber()
                    gameViewModel.generateNumbers()
                } else {
                    if (gameViewModel.score.value!! > gameViewModel.bestScore.value!!) {
                        saveScore()
                    }

                    showGameOverDialog()

                    mediaPlayer.pause()
                }

                gameViewModel.onSecondNumberMBTNClickComplete()
            }
        })

        gameViewModel.eventThirdNumberMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                if (gameViewModel.numbers.value!![2] == gameViewModel.catchableNumber.value) {
                    gameViewModel.increaseScore()

                    gameViewModel.generateCatchableNumber()
                    gameViewModel.generateNumbers()
                } else {
                    if (gameViewModel.score.value!! > gameViewModel.bestScore.value!!) {
                        saveScore()
                    }

                    showGameOverDialog()

                    mediaPlayer.pause()
                }

                gameViewModel.onThirdNumberMBTNClickComplete()
            }
        })

        gameViewModel.eventFourthNumberMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                if (gameViewModel.numbers.value!![3] == gameViewModel.catchableNumber.value) {
                    gameViewModel.increaseScore()

                    gameViewModel.generateCatchableNumber()
                    gameViewModel.generateNumbers()
                } else {
                    if (gameViewModel.score.value!! > gameViewModel.bestScore.value!!) {
                        saveScore()
                    }

                    showGameOverDialog()

                    mediaPlayer.pause()
                }

                gameViewModel.onFourthNumberMBTNClickComplete()
            }
        })

        gameViewModel.eventTryAgainMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                gameOverDialog.dismiss()

                navigate(GameFragmentDirections.actionGameFragmentSelf())

                gameViewModel.onTryAgainMBTNClickComplete()
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

    private fun getBestScore() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(
            SCORE_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        gameViewModel.setBestScore(sharedPreferences.getInt(SCORE_KEY, 0))
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

    private fun navigate(navDirections: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(navDirections) }
    }

    override fun onPause() {
        super.onPause()

        mediaPlayer.pause()
        gameViewModel.setCurrentPosition(mediaPlayer.currentPosition)
    }

    override fun onResume() {
        super.onResume()

        if (gameViewModel.currentPosition.value == null) {
            mediaPlayer.start()
        } else {
            if(!pauseDialog.isShowing && !gameOverDialog.isShowing){
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

}
