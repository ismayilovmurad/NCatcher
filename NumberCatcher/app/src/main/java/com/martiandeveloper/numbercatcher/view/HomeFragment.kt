package com.martiandeveloper.numbercatcher.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.martiandeveloper.numbercatcher.viewmodel.HomeViewModel
import com.martiandeveloper.numbercatcher.R
import com.martiandeveloper.numbercatcher.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        homeViewModel = getViewModel()
        binding.homeViewModel = homeViewModel
        binding.lifecycleOwner = this
        observe()
    }

    private fun getViewModel(): HomeViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel() as T
            }
        })[HomeViewModel::class.java]
    }

    private fun observe() {
        homeViewModel.eventStartMBTNClick.observe(viewLifecycleOwner, {
            if (it) {
                navigate(HomeFragmentDirections.actionHomeFragmentToGameFragment())
            }
        })
    }

    private fun navigate(navDirections: NavDirections) {
        view?.let { Navigation.findNavController(it).navigate(navDirections) }
        homeViewModel.onStartMBTNClickComplete()
    }

}
