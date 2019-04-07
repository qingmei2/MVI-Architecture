package com.github.qingmei2.mvi.base.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.uber.autodispose.autoDisposable
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

@Suppress("LeakingThis")
open class AutoDisposeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    LifecycleScopeProvider<AutoDisposeViewHolder.ViewHolderEvent> {

    private val lifecycleEvents: BehaviorSubject<AutoDisposeViewHolder.ViewHolderEvent> =
        BehaviorSubject.createDefault(AutoDisposeViewHolder.ViewHolderEvent.OnBinds)

    sealed class ViewHolderEvent {

        object OnBinds : ViewHolderEvent()

        data class OnUnbindsPosition(val position: Int) : ViewHolderEvent()

        object OnUnbindsForce : ViewHolderEvent()
    }

    fun resetBindEvents(eventProvider: AutoDisposeViewHolderEventsProvider) {
        lifecycleEvents.onNext(AutoDisposeViewHolder.ViewHolderEvent.OnBinds)
        eventProvider
            .providesObservable()
            .map { event ->
                when (event) {
                    is ViewHolderEvent.OnUnbindsPosition -> {
                        val current = event.position == adapterPosition
                        when (current) {
                            true -> ViewHolderEvent.OnUnbindsForce
                            false -> event
                        }
                    }
                    is ViewHolderEvent.OnBinds -> event
                    is ViewHolderEvent.OnUnbindsForce -> event
                }
            }
            .filter { it !is ViewHolderEvent.OnUnbindsPosition }
            .autoDisposable(this)
            .subscribe(lifecycleEvents)
    }

    override fun lifecycle(): Observable<ViewHolderEvent> {
        return lifecycleEvents.hide()
    }

    override fun correspondingEvents(): CorrespondingEventsFunction<ViewHolderEvent> {
        return CORRESPONDING_EVENTS
    }

    override fun peekLifecycle(): ViewHolderEvent? {
        return lifecycleEvents.value
    }

    companion object {

        private val CORRESPONDING_EVENTS = CorrespondingEventsFunction<AutoDisposeViewHolder.ViewHolderEvent> { event ->
            when (event) {
                AutoDisposeViewHolder.ViewHolderEvent.OnBinds ->
                    AutoDisposeViewHolder.ViewHolderEvent.OnUnbindsForce
                else -> throw LifecycleEndedException(
                    "Cannot binds lifecycle after onUnbinds."
                )
            }
        }
    }
}