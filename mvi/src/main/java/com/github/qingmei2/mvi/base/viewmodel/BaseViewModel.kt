package com.github.qingmei2.mvi.base.viewmodel

import com.github.qingmei2.mvi.base.intent.IIntent
import com.github.qingmei2.mvi.base.viewstate.IViewState

abstract class BaseViewModel<I : IIntent, VS : IViewState> : AutoDisposeViewModel(), IViewModel<I, VS>