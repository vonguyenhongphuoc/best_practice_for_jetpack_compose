package com.devhp.bestpracticecomposesample

sealed class CounterEvent {
    data class ValueEntered(val value: String) : CounterEvent()
    data object CountButtonClicked : CounterEvent()
    data object ResetButtonClicked : CounterEvent()
}