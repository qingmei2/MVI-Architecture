package com.github.qingmei2.mvi.base.view.activity

import android.os.Bundle
import com.github.qingmei2.mvi.base.intent.IIntent
import com.github.qingmei2.mvi.base.view.IView
import com.github.qingmei2.mvi.base.viewstate.IViewState

abstract class BaseActivity<I : IIntent, in S : IViewState> : InjectionActivity()
    , IView<I, S> {

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }
}