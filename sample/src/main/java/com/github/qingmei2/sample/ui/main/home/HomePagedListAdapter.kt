package com.github.qingmei2.sample.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.entity.ReceivedEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HomePagedListAdapter : PagedListAdapter<ReceivedEvent, HomePagedListViewHolder>(diffCallback) {

    private val eventSubject: PublishSubject<HomePagedListItemEvent> = PublishSubject.create()

    fun observeEvent(): Observable<HomePagedListItemEvent> = eventSubject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePagedListViewHolder =
        HomePagedListViewHolder.create(parent)

    override fun onBindViewHolder(holder: HomePagedListViewHolder, position: Int) =
        holder.binds(getItem(position)!!, eventSubject)

    companion object {
        private val diffCallback: DiffUtil.ItemCallback<ReceivedEvent> =
            object : DiffUtil.ItemCallback<ReceivedEvent>() {

                override fun areItemsTheSame(oldItem: ReceivedEvent, newItem: ReceivedEvent): Boolean =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: ReceivedEvent, newItem: ReceivedEvent): Boolean =
                    oldItem.id == newItem.id
            }
    }
}

class HomePagedListViewHolder(private val rootView: View) : RecyclerView.ViewHolder(rootView) {

    fun binds(
        data: ReceivedEvent,
        subject: PublishSubject<HomePagedListItemEvent>
    ) {
        rootView.setOnClickListener {
            subject.onNext(HomePagedListItemEvent.ClickEvent(data))
        }
    }

    companion object {

        fun create(parent: ViewGroup): HomePagedListViewHolder =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_received_event, parent, false)
                .run(::HomePagedListViewHolder)
    }
}

sealed class HomePagedListItemEvent {

    data class ClickEvent(val data: ReceivedEvent) : HomePagedListItemEvent()

}