package com.github.qingmei2.sample.ui.main.profile

import com.github.qingmei2.mvi.ext.reactivex.flatMapErrorActionObservable
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.manager.UserManager
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class ProfileActionProcessorHolder(
    private val repository: ProfileRepository,
    private val schedulerProvider: SchedulerProvider
) {

    private val initialActionTransformer: ObservableTransformer<ProfileAction.InitialAction, ProfileResult>
        get() = ObservableTransformer { action ->
            action
                .flatMap<ProfileResult> {
                    // use local data, use HTTP request or else instead of it at production environment.
                    Observable.just(UserManager.INSTANCE)
                        .map(ProfileResult.InitialResult::Success)
                }
                .startWith(ProfileResult.InitialResult.InFlight)
        }

    val actionProcessor: ObservableTransformer<ProfileAction, ProfileResult> =
        ObservableTransformer { actions ->
            actions.publish { shared ->
                Observable.merge(
                    shared.ofType(ProfileAction.InitialAction::class.java).compose(initialActionTransformer),
                    shared.filter { o ->
                        o !is ProfileAction.InitialAction
                    }.flatMapErrorActionObservable()
                )
            }
        }
}