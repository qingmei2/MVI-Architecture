package com.github.qingmei2.mvi.base.viewmodel

import com.github.qingmei2.mvi.base.intent.IIntent
import com.github.qingmei2.mvi.base.viewstate.IViewState
import io.reactivex.Observable

interface IViewModel<I : IIntent, VS : IViewState> {

    fun processIntents(intents: Observable<I>)

    fun states(): Observable<VS>
}