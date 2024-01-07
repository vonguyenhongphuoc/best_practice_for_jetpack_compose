package com.devhp.bestpracticecomposesample

import android.media.metrics.Event
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch

class CounterViewModel : ViewModel() {
    private val _screenState =
        mutableStateOf(MainScreenState(inputValue = "", displayResult = "Total is 0.0"))
    val screenState: State<MainScreenState> = _screenState


    private val _uiEventFlow = MutableSharedFlow<UIEvent>()
    val uiEventFlow = _uiEventFlow.asSharedFlow()

    private var total = 0.0

    private fun addToTotal() {
        total += _screenState.value.inputValue.toDouble()
        _screenState.value =
            _screenState.value.copy(
                displayResult = "Total is $total",
                isCountButtonVisible = false,
                inputValue = ""
            )
    }

    private fun resetTotal() {
        total = 0.0
        _screenState.value = _screenState.value.copy(
            displayResult = "Total is $total",
            inputValue = "",
            isCountButtonVisible = false
        )
    }

    fun onEvent(event: CounterEvent) {
        when (event) {
            is CounterEvent.ValueEntered -> {
                _screenState.value = _screenState.value.copy(
                    inputValue = event.value,
                    isCountButtonVisible = true
                )
            }

            is CounterEvent.CountButtonClicked -> {
                addToTotal()
                viewModelScope.launch {
                    _uiEventFlow.emit(
                        UIEvent.ShowMessage(message = "Value added successfully")
                    )
                }
            }

            is CounterEvent.ResetButtonClicked -> {
                resetTotal()
                viewModelScope.launch {
                    _uiEventFlow.emit(
                        UIEvent.ShowMessage(message = "Value reset successfully")
                    )
                }
            }
        }
    }

    private val eventInt = MutableSharedFlow<Int>()
    private val channel = Channel<Int>()
    init {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MyTag", "collect")
            val last = channel.receive()
            Log.d("last", last.toString())
        }

        Log.d("MyTag", "Init ViewModel")
        viewModelScope.launch(Dispatchers.IO) {
            for (i in 1..1000) {
                Log.d("Emit", i.toString())
                eventInt.emit(i)
                if (i == 999) {
                    channel.send(i)
                    channel.close()
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val flow = flow {
                emit(1)
                delay(50)
                emit(2)
            }

            flow.collect { value ->
                println("AX Collecting $value")
                delay(100) // Emulate work
                println("AX $value collected")

            }
// Output: "Collecting 1", "1 collected", "Collecting 2", "2 collected"
            println("AX.........................")
            flow.collectLatest { value ->
                println("AX Collecting $value")
                delay(100) // Emulate work
                println("AX $value collected")
            }
        }
// Output: "Collecting 1", "Collecting 2", "2 collected"
    }


}