package com.github.qingmei2.sample.ui.main.home

import com.github.qingmei2.mvi.base.viewstate.IViewState

data class HomeViewState(
    val content: String
) : IViewState