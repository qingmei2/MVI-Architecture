package com.github.qingmei2.sample.ui.main.home

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.qingmei2.mvi.image.GlideApp
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.entity.ReceivedEvent
import com.github.qingmei2.sample.entity.Type
import com.github.qingmei2.sample.utils.TimeConverter
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

class HomePagedListViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {

    private val mIvAvatar: ImageView = rootView.findViewById(R.id.ivAvatar)
    private val mTvEvent: TextView = rootView.findViewById(R.id.tvEventContent)
    private val mTvEventTime: TextView = rootView.findViewById(R.id.tvEventTime)
    private val mIvEventType: ImageView = rootView.findViewById(R.id.ivEventType)

    fun binds(
        data: ReceivedEvent,
        subject: PublishSubject<HomePagedListItemEvent>
    ) {
        GlideApp.with(mIvAvatar.context)
            .load(data.actor.avatarUrl)
            .into(mIvAvatar)

        renderEvent(mTvEvent, data, subject)

        mIvEventType.setImageDrawable(
            when (data.type) {
                Type.WatchEvent ->
                    ContextCompat.getDrawable(mIvEventType.context, R.mipmap.ic_star_yellow_light)
                Type.CreateEvent, Type.ForkEvent, Type.PushEvent ->
                    ContextCompat.getDrawable(mIvEventType.context, R.mipmap.ic_fork_green_light)
                else -> null
            }
        )

        mTvEventTime.text = TimeConverter.tramsTimeAgo(data.createdAt)
    }

    private fun renderEvent(
        view: TextView,
        data: ReceivedEvent,
        subject: PublishSubject<HomePagedListItemEvent>
    ) {
        val actor = data.actor.displayLogin
        val action = when (data.type) {
            Type.WatchEvent -> "starred"
            Type.CreateEvent -> "created"
            Type.ForkEvent -> "forked"
            Type.PushEvent -> "pushed"
            else -> data.type.name
        }
        val repo = data.repo.name

        val actorSpan = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                subject.onNext(HomePagedListItemEvent.ClickEvent(data.actor.url))
            }
        }
        val repoSpan = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                subject.onNext(HomePagedListItemEvent.ClickEvent(data.repo.url))
            }
        }
        val styleSpan = StyleSpan(Typeface.BOLD)
        val styleSpan2 = StyleSpan(Typeface.BOLD)

        view.movementMethod = LinkMovementMethod.getInstance()
        view.text = SpannableStringBuilder().apply {
            append("$actor $action $repo")
            setSpan(actorSpan, 0, actor.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(styleSpan, 0, actor.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(
                repoSpan,
                actor.length + action.length + 2,
                actor.length + action.length + repo.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                styleSpan2,
                actor.length + action.length + 2,
                actor.length + action.length + repo.length + 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
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

    data class ClickEvent(val url: String) : HomePagedListItemEvent()

}