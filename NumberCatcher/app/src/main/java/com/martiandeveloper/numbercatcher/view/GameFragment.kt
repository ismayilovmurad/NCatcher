package com.martiandeveloper.numbercatcher.view

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.martiandeveloper.numbercatcher.R
import com.martiandeveloper.numbercatcher.databinding.DialogPauseBinding
import com.martiandeveloper.numbercatcher.databinding.FragmentGameBinding
import com.martiandeveloper.numbercatcher.viewmodel.GameViewModel
import timber.log.Timber

class GameFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var pauseDialog: AlertDialog

    private lateinit var mainBinding: FragmentGameBinding

    private lateinit var gameViewModel: GameViewModel

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
            try {
                if (!pauseDialog.isShowing) {
                    showPauseDialog()
                }
            } catch (e: Exception) {
                showPauseDialog()
            }
        }
    }

    private fun showPauseDialog() {
        pauseDialog = AlertDialog.Builder(context).create()
        val binding = DialogPauseBinding.inflate(LayoutInflater.from(context))

        binding.gameViewModel = gameViewModel
        binding.lifecycleOwner = this

        pauseDialog.setView(binding.root)
        pauseDialog.setCanceledOnTouchOutside(false)
        pauseDialog.setCancelable(false)
        pauseDialog.show()
    }

}
