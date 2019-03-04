package com.github.qingmei2.sample.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.qingmei2.mvi.base.viewmodel.BaseViewModel
import com.github.qingmei2.mvi.ext.reactivex.notOfType
import com.github.qingmei2.mvi.util.SingletonHolderSingleArg
import com.github.qingmei2.sample.ui.main.repos.ReposUIEvent
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class HomeViewModel(
    private val actionProcessorHolder: HomeActionProcessorHolder
) : BaseViewModel<HomeIntent, HomeViewState>() {

    private val intentSubject: PublishSubject<HomeIntent> = PublishSubject.create()
    private val statesObservable: Observable<HomeViewState> = compose()

    private val intentFilter: ObservableTransformer<HomeIntent, HomeIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(HomeIntent.InitialIntent::class.java).take(1),
                    shared.notOfType(HomeIntent.InitialIntent::class.java)
                )
            }
        }

    private fun actionFromIntent(intent: HomeIntent): HomeAction {
        return when (intent) {
            is HomeIntent.InitialIntent -> HomeAction.InitialAction
            HomeIntent.ScrollToTopIntent -> HomeAction.ScrollToTopAction
            is HomeIntent.ScrollStateChangedIntent -> HomeAction.ScrollStateChangedAction(intent.state)
        }
    }

    override fun processIntents(intents: Observable<HomeIntent>) {
        intents.autoDisposable(this).subscribe(intentSubject)
    }

    override fun states(): Observable<HomeViewState> = statesObservable

    private fun compose(): Observable<HomeViewState> {
        return intentSubject
            .compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(HomeViewState.idle(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    companion object {

        private val reducer = BiFunction { previousState: HomeViewState, result: HomeResult ->
            when (result) {
                is HomeResult.InitialResult -> when (result) {
                    is HomeResult.InitialResult.Success ->
                        previousState.copy(
                            error = null,
                            isRefreshing = false,
                            uiEvent = HomeUIEvent.InitialSuccess(result.pagedList)
                        )
                    is HomeResult.InitialResult.Failure ->
                        previousState.copy(
                            error = result.error,
                            isRefreshing = false,
                            uiEvent = null
                        )
                    is HomeResult.InitialResult.InFlight ->
                        previousState.copy(
                            error = null,
                            isRefreshing = true,
                            uiEvent = null
                        )
                }
                is HomeResult.FloatActionButtonVisibleResult ->
                    previousState.copy(
                        error = null,
                        isRefreshing = false,
                        uiEvent = HomeUIEvent.FloatActionButtonEvent(result.visible)
                    )
                HomeResult.ScrollToTopResult ->
                    previousState.copy(
                        error = null,
                        isRefreshing = false,
                        uiEvent = HomeUIEvent.ScrollToTopEvent
                    )
            }
        }
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