package com.github.qingmei2.mvi.base.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.qingmei2.mvi.base.intent.IIntent
import com.github.qingmei2.mvi.base.view.IView
import com.github.qingmei2.mvi.base.viewstate.IViewState

abstract class BaseFragment<I : IIntent, in S : IViewState> : InjectionFragment()
    , IView<I, S> {

    private var mRootView: View? = null

    abstract val layoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = LayoutInflater.from(context).inflate(layoutId, container, false)
        return mRootView!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRootView = null
    }
}
