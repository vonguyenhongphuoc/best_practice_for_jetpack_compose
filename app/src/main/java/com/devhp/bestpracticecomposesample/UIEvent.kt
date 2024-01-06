package com.devhp.bestpracticecomposesample

sealed class UIEvent {
    data class ShowMessage(val message: String) : UIEvent()
}