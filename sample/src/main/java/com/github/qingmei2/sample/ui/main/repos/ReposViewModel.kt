package com.github.qingmei2.sample.ui.main.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.qingmei2.mvi.base.viewmodel.BaseViewModel
import com.github.qingmei2.mvi.ext.reactivex.notOfType
import com.github.qingmei2.mvi.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

@SuppressWarnings("checkResult")
class ReposViewModel(
    private val actionProcessorHolder: ReposActionProcessorHolder
) : BaseViewModel<ReposIntent, ReposViewState>() {

    private val intentsSubject: PublishSubject<ReposIntent> = PublishSubject.create()
    private val statesObservable: Observable<ReposViewState> = compose()

    private val sortOptionSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val intentFilter: ObservableTransformer<ReposIntent, ReposIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(ReposIntent.InitialIntent::class.java).take(1),
                    shared.notOfType(ReposIntent.InitialIntent::class.java)
                )
            }
        }

    private val sortOptionEventFilter: ObservableTransformer<ReposIntent, ReposIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.mergeArray(
                    shared.ofType(ReposIntent.InitialIntent::class.java)
                        .doOnNext { sortOptionSubject.onNext(sortByUpdate) },
                    shared.ofType(ReposIntent.SortTypeChangeIntent::class.java)
                        .doOnNext { sortOptionSubject.onNext(it.sort) },
                    shared.ofType(ReposIntent.RefreshIntent::class.java),
                    shared.ofType(ReposIntent.ScrollStateChangedIntent::class.java),
                    shared.ofType(ReposIntent.ScrollToTopIntent::class.java)
                )
            }
        }


    override fun processIntents(intents: Observable<ReposIntent>) {
        intents.autoDisposable(this).subscribe(intentsSubject)
    }

    override fun states(): Observable<ReposViewState> {
        return statesObservable
    }

    private fun compose(): Observable<ReposViewState> {
        return intentsSubject
            .compose(intentFilter)
            .compose(sortOptionEventFilter)
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(ReposViewState.idle(), ReposViewModel.reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun currentSortOptions(): Observable<String> {
        return sortOptionSubject.distinctUntilChanged().take(1)
    }

    private fun actionFromIntent(intent: ReposIntent): ReposAction {
        return when (intent) {
            is ReposIntent.InitialIntent,
            is ReposIntent.RefreshIntent,
            is ReposIntent.SortTypeChangeIntent -> ReposAction.QueryReposAction(currentSortOptions().blockingLast())
            is ReposIntent.ScrollToTopIntent -> ReposAction.ScrollToTopAction
            is ReposIntent.ScrollStateChangedIntent -> ReposAction.ScrollStateChangedAction(intent.type)
        }
    }

    companion object {

        const val sortByCreated: String = "created"

        const val sortByUpdate: String = "updated"

        const val sortByLetter: String = "full_name"

        private val reducer = BiFunction { previousState: ReposViewState, result: ReposResult ->
            when (result) {
                is ReposResult.QueryReposResult -> when (result) {
                    is ReposResult.QueryReposResult.Success ->
                        previousState.copy(
                            error = null,
                            isRefreshing = false,
                            uiEvent = ReposUIEvent.InitialSuccess(result.pagedList)
                        )
                    is ReposResult.QueryReposResult.Failure ->
                        previousState.copy(
                            error = result.error,
                            isRefreshing = false,
                            uiEvent = null
                        )
                    is ReposResult.QueryReposResult.InFlight ->
                        previousState.copy(
                            error = null,
                            isRefreshing = true,
                            uiEvent = null
                        )
                }
                is ReposResult.FloatActionButtonVisibleResult ->
                    previousState.copy(
                        error = null,
                        isRefreshing = false,
                        uiEvent = ReposUIEvent.FloatActionButtonEvent(result.visible)
                    )
                ReposResult.ScrollToTopResult ->
                    previousState.copy(
                        error = null,
                        isRefreshing = false,
                        uiEvent = ReposUIEvent.ScrollToTopEvent
                    )
            }
        }
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