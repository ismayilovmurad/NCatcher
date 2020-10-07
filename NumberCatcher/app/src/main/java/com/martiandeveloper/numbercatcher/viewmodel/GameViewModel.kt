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

    private var _numbers = MutableLiveData<ArrayList<Int>>()
    val numbers: LiveData<ArrayList<Int>>
        get() = _numbers

    private var _eventFirstNumberMBTNClick = MutableLiveData<Boolean>()
    val eventFirstNumberMBTNClick: LiveData<Boolean>
        get() = _eventFirstNumberMBTNClick

    private var _eventSecondNumberMBTNClick = MutableLiveData<Boolean>()
    val eventSecondNumberMBTNClick: LiveData<Boolean>
        get() = _eventSecondNumberMBTNClick

    private var _eventThirdNumberMBTNClick = MutableLiveData<Boolean>()
    val eventThirdNumberMBTNClick: LiveData<Boolean>
        get() = _eventThirdNumberMBTNClick

    private var _eventFourthNumberMBTNClick = MutableLiveData<Boolean>()
    val eventFourthNumberMBTNClick: LiveData<Boolean>
        get() = _eventFourthNumberMBTNClick

    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private var _bestScore = MutableLiveData<Int>()
    val bestScore: LiveData<Int>
        get() = _bestScore

    private var _eventTryAgainMBTNClick = MutableLiveData<Boolean>()
    val eventTryAgainMBTNClick: LiveData<Boolean>
        get() = _eventTryAgainMBTNClick

    private var _eventHome2MBTNClick = MutableLiveData<Boolean>()
    val eventHome2MBTNClick: LiveData<Boolean>
        get() = _eventHome2MBTNClick


    init {
        _catchableNumber.value = 6

        generateNumbers()

        _score.value = 0

        _bestScore.value = 0
    }


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

    fun generateCatchableNumber() {
        val start = catchableNumber.value!!.plus(8)
        val end = catchableNumber.value!!.plus(8).plus(16)
        _catchableNumber.value = (start..end).random()
    }

    fun generateNumbers() {

        val start = catchableNumber.value!!.minus(8)
        val end = catchableNumber.value!!.plus(8)

        val list = ArrayList<Int>()
        list.add(catchableNumber.value!!)

        for (i in 0 until 3) {
            var random = (start..end).random()

            while (list.contains(random)) {
                random = (start..end).random()
            }

            if (!list.contains(random)) {
                list.add(random)
            }

        }

        list.shuffle()

        _numbers.value = list

    }

    fun onFirstNumberMBTNClick() {
        _eventFirstNumberMBTNClick.value = true
    }

    fun onFirstNumberMBTNClickComplete() {
        _eventFirstNumberMBTNClick.value = false
    }

    fun onSecondNumberMBTNClick() {
        _eventSecondNumberMBTNClick.value = true
    }

    fun onSecondNumberMBTNClickComplete() {
        _eventSecondNumberMBTNClick.value = false
    }

    fun onThirdNumberMBTNClick() {
        _eventThirdNumberMBTNClick.value = true
    }

    fun onThirdNumberMBTNClickComplete() {
        _eventThirdNumberMBTNClick.value = false
    }

    fun onFourthNumberMBTNClick() {
        _eventFourthNumberMBTNClick.value = true
    }

    fun onFourthNumberMBTNClickComplete() {
        _eventFourthNumberMBTNClick.value = false
    }

    fun increaseScore() {
        _score.value = _score.value!!.plus(1)
    }

    fun setBestScore(score: Int) {
        _bestScore.value = score
    }

    fun onTryAgainMBTNClick() {
        _eventTryAgainMBTNClick.value = true
    }

    fun onTryAgainMBTNClickComplete() {
        _eventTryAgainMBTNClick.value = false
    }

    fun onHome2MBTNClick() {
        _eventHome2MBTNClick.value = true
    }

    fun onHome2MBTNClickComplete() {
        _eventHome2MBTNClick.value = false
    }

}
