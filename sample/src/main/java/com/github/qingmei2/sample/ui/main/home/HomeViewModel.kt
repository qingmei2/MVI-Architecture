package com.github.qingmei2.sample.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.qingmei2.mvi.base.viewmodel.BaseViewModel
import com.github.qingmei2.mvi.util.SingletonHolderSingleArg
import io.reactivex.Observable

class HomeViewModel(
    private val actionProcessorHolder: HomeActionProcessorHolder
) : BaseViewModel<HomeIntent, HomeViewState>() {

    override fun processIntents(intents: Observable<HomeIntent>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun states(): Observable<HomeViewState> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory private constructor(
    private val actionProcessorHolder: HomeActionProcessorHolder
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        HomeViewModel(actionProcessorHolder) as T

    companion object :
        SingletonHolderSingleArg<HomeViewModelFactory, HomeActionProcessorHolder>(::HomeViewModelFactory)
}