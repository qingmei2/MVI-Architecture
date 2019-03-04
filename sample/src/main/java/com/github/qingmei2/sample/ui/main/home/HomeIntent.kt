package com.github.qingmei2.sample.ui.main.home

import com.github.qingmei2.mvi.base.intent.IIntent

sealed class HomeIntent : IIntent {

    object InitialIntent : HomeIntent()

    object RefreshIntent : HomeIntent()

    object ScrollToTopIntent : HomeIntent()

    data class ScrollStateChangedIntent(val state: Int) : HomeIntent()
}