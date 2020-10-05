package com.martiandeveloper.numbercatcher.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private var _eventContinueMBTNClick = MutableLiveData<Boolean>()
    val eventContinueMBTNClick: LiveData<Boolean>
        get() = _eventContinueMBTNClick

    private var _eventHomeMBTNClick = MutableLiveData<Boolean>()
    val eventHomeMBTNClick: LiveData<Boolean>
        get() = _eventHomeMBTNClick

    private var _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int>
        get() = _currentPosition

    private var _catchableNumber = MutableLiveData<Int>()
    val catchableNumber: LiveData<Int>
        get() = _catchableNumber

    fun onContinueMBTNClick() {
        _eventContinueMBTNClick.value = true
    }

    fun onContinueMBTNClickComplete() {
        _eventContinueMBTNClick.value = false
    }

    fun onHomeMBTNClick() {
        _eventHomeMBTNClick.value = true
    }

    fun onHomeMBTNClickComplete() {
        _eventHomeMBTNClick.value = false
    }

    fun setCurrentPosition(position: Int) {
        _currentPosition.value = position
    }

    fun setCatchableNumber(number: Int) {
        _catchableNumber.value = number
    }
}
