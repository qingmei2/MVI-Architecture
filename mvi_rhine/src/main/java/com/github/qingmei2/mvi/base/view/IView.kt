package com.github.qingmei2.mvi.base.view

import com.github.qingmei2.mvi.base.intent.IIntent
import com.github.qingmei2.mvi.base.viewstate.IViewState
import io.reactivex.Observable

interface IView<I : IIntent, in S : IViewState> {

    fun intents(): Observable<I>

    fun render(state: S)
}
