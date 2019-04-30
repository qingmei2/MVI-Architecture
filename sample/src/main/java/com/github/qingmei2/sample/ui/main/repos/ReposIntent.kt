package com.github.qingmei2.sample.ui.main.repos

import com.github.qingmei2.mvi.base.intent.IIntent

sealed class ReposIntent : IIntent {

    object InitialIntent : ReposIntent()

    object RefreshIntent : ReposIntent()

    data class SortTypeChangeIntent(val sort: String) : ReposIntent()

    object ScrollToTopIntent : ReposIntent()

    data class ScrollStateChangedIntent(val type: Int) : ReposIntent()
}