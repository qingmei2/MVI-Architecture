package com.github.qingmei2.mvi.base.view.adapter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

@Suppress("LeakingThis")
abstract class AutoDisposeAdapter<VH : RecyclerView.ViewHolder>(lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<VH>(), LifecycleScopeProvider<AutoDisposeAdapter.AdapterEvent>,
    DefaultLifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    enum class AdapterEvent {
        ON_CREATED, ON_DESTROY
    }

    private val lifecycleEvents: BehaviorSubject<AutoDisposeAdapter.AdapterEvent> =
        BehaviorSubject.createDefault(AutoDisposeAdapter.AdapterEvent.ON_CREATED)
    val autoDisposeViewHolderEvents: PublishSubject<AutoDisposeViewHolder.ViewHolderEvent> =
        PublishSubject.create()

    override fun lifecycle(): Observable<AutoDisposeAdapter.AdapterEvent> {
        return lifecycleEvents.hide()
    }

    override fun correspondingEvents(): CorrespondingEventsFunction<AdapterEvent> {
        return CORRESPONDING_EVENTS
    }

    override fun peekLifecycle(): AdapterEvent? {
        return lifecycleEvents.value
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        when (holder) {
            is AutoDisposeViewHolder -> postViewHolderEvent(AutoDisposeViewHolder.ViewHolderEvent.ON_BINDS)
        }
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        when (holder) {
            is AutoDisposeViewHolder -> postViewHolderEvent(AutoDisposeViewHolder.ViewHolderEvent.ON_UNBINDS)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        lifecycleEvents.onNext(AutoDisposeAdapter.AdapterEvent.ON_DESTROY)
        postViewHolderEvent(AutoDisposeViewHolder.ViewHolderEvent.ON_UNBINDS)
    }

    private fun postViewHolderEvent(viewHolderEvent: AutoDisposeViewHolder.ViewHolderEvent) {
        autoDisposeViewHolderEvents.onNext(viewHolderEvent)
    }

    companion object {

        private val CORRESPONDING_EVENTS = CorrespondingEventsFunction<AutoDisposeAdapter.AdapterEvent> { event ->
            when (event) {
                AutoDisposeAdapter.AdapterEvent.ON_CREATED ->
                    AutoDisposeAdapter.AdapterEvent.ON_DESTROY
                else -> throw LifecycleEndedException(
                    "Cannot bind to ViewModel lifecycle after onCleared."
                )
            }
        }
    }
}