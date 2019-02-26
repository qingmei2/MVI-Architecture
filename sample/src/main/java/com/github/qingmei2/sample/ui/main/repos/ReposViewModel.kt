package com.github.qingmei2.sample.ui.main.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.qingmei2.mvi.base.viewmodel.BaseViewModel
import com.github.qingmei2.mvi.util.SingletonHolderSingleArg
import io.reactivex.Observable

@SuppressWarnings("checkResult")
class ReposViewModel(
    private val actionProcessorHolder: ReposActionProcessorHolder
) : BaseViewModel<ReposIntent, ReposViewState>() {

    override fun processIntents(intents: Observable<ReposIntent>) {

    }

    override fun states(): Observable<ReposViewState> {
        return Observable.empty()
    }

    companion object {

        const val sortByCreated: String = "created"

        const val sortByUpdate: String = "updated"

        const val sortByLetter: String = "full_name"
    }
}

class ReposViewModelFactory(
    private val actionProcessorHolder: ReposActionProcessorHolder
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReposViewModel(actionProcessorHolder) as T
    }

    companion object :
        SingletonHolderSingleArg<ReposViewModelFactory, ReposActionProcessorHolder>(::ReposViewModelFactory)
}