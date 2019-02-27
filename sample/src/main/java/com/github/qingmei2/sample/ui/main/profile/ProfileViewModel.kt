package com.github.qingmei2.sample.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.qingmei2.mvi.base.viewmodel.BaseViewModel
import com.github.qingmei2.mvi.ext.reactivex.notOfType
import com.github.qingmei2.mvi.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class ProfileViewModel(
    private val actionProcessorHolder: ProfileActionProcessorHolder
) : BaseViewModel<ProfileIntent, ProfileViewState>() {

    private val intentsSubject: PublishSubject<ProfileIntent> = PublishSubject.create()
    private val statesObservable: Observable<ProfileViewState> = compose()

    private val intentFilter: ObservableTransformer<ProfileIntent, ProfileIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(ProfileIntent.InitialIntent::class.java).take(1),
                    shared.notOfType(ProfileIntent.InitialIntent::class.java)
                )
            }
        }

    override fun processIntents(intents: Observable<ProfileIntent>) {
        intents.autoDisposable(this).subscribe(intentsSubject)
    }

    override fun states(): Observable<ProfileViewState> {
        return statesObservable
    }

    private fun compose(): Observable<ProfileViewState> {
        return intentsSubject
            .compose(intentFilter)
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(ProfileViewState.idle(), ProfileViewModel.reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: ProfileIntent): ProfileAction {
        return when (intent) {
            is ProfileIntent.InitialIntent -> ProfileAction.InitialAction
        }
    }

    companion object {

        private val reducer = BiFunction { previousState: ProfileViewState, result: ProfileResult ->
            when (result) {
                is ProfileResult.InitialResult -> when (result) {
                    is ProfileResult.InitialResult.Success ->
                        previousState.copy(
                            error = null,
                            isRefreshing = false,
                            uiEvent = ProfileUIEvent.InitialSuccess(result.user)
                        )
                    is ProfileResult.InitialResult.Failure ->
                        previousState.copy(
                            error = result.error,
                            isRefreshing = false,
                            uiEvent = null
                        )
                    is ProfileResult.InitialResult.InFlight ->
                        previousState.copy(
                            error = null,
                            isRefreshing = true,
                            uiEvent = null
                        )
                }
            }
        }
    }
}

class ProfileViewModelFactory(
    private val actionProcessorHolder: ProfileActionProcessorHolder
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(actionProcessorHolder) as T
    }

    companion object :
        SingletonHolderSingleArg<ProfileViewModelFactory, ProfileActionProcessorHolder>(::ProfileViewModelFactory)
}