package com.devhp.bestpracticecomposesample

data class MainScreenState(
    var isCountButtonVisible: Boolean = false,
    var displayResult: String = "",
    var inputValue : String = ""
)