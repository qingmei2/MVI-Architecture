package com.github.qingmei2.sample.ui.main.repos

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.github.qingmei2.mvi.base.view.adapter.AutoDisposeViewHolder
import com.github.qingmei2.mvi.image.GlideApp
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.entity.Repo
import com.jakewharton.rxbinding3.view.clicks
import com.uber.autodispose.autoDisposable
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ReposPagedListAdapter : PagedListAdapter<Repo, ReposPagedListViewHolder>(diffCallback) {

    private val eventSubject: PublishSubject<RepoPagedListItemEvent> = PublishSubject.create()

    fun observeEvent(): Observable<RepoPagedListItemEvent> = eventSubject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposPagedListViewHolder =
        ReposPagedListViewHolder.create(parent)

    override fun onBindViewHolder(holder: ReposPagedListViewHolder, position: Int) {
        holder.binds(getItem(position)!!, eventSubject)
    }

    companion object {
        private val diffCallback: DiffUtil.ItemCallback<Repo> =
            object : DiffUtil.ItemCallback<Repo>() {

                override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                    oldItem.id == newItem.id
            }
    }
}

class ReposPagedListViewHolder(rootView: View) : AutoDisposeViewHolder(rootView) {

    private val mBtnAvatar: ConstraintLayout = rootView.findViewById(R.id.btnAvatar)
    private val mBtnRoot: ConstraintLayout = rootView.findViewById(R.id.btnRoot)
    private val mIvAvatar: ImageView = rootView.findViewById(R.id.ivAvatar)
    private val mTvOwnerName: TextView = rootView.findViewById(R.id.tvOwnerName)
    private val mTvRepoName: TextView = rootView.findViewById(R.id.tvRepoName)
    private val mTvLanguage: TextView = rootView.findViewById(R.id.tvLanguage)
    private val mIvLanguageColor: CircleImageView = rootView.findViewById(R.id.ivLanguageColor)
    private val mTvRepoDesc: TextView = rootView.findViewById(R.id.tvRepoDesc)
    private val mTvStar: TextView = rootView.findViewById(R.id.tvStar)
    private val mTvIssue: TextView = rootView.findViewById(R.id.tvIssue)
    private val mTvFork: TextView = rootView.findViewById(R.id.tvFork)

    fun binds(
        data: Repo,
        subject: PublishSubject<RepoPagedListItemEvent>
    ) {
        mBtnRoot.clicks()
            .map { RepoPagedListItemEvent.ClickEvent(data.htmlUrl) }
            .autoDisposable(this)
            .subscribe(subject)
        mBtnAvatar.clicks()
            .map { RepoPagedListItemEvent.ClickEvent(data.owner.htmlUrl) }
            .autoDisposable(this)
            .subscribe(subject)

        GlideApp.with(mIvAvatar.context)
            .load(data.owner.avatarUrl)
            .into(mIvAvatar)

        mTvOwnerName.text = data.owner.login
        mIvLanguageColor.visibility = if (data.language == null) View.GONE else View.VISIBLE
        mIvLanguageColor.setImageDrawable(getLanguageColor(data.language))

        mTvLanguage.text = data.language ?: ""

        mTvRepoName.text = data.fullName
        mTvRepoDesc.text = data.description ?: "(No description, website, or topics provided.)"
        mTvStar.text = data.stargazersCount.toString()
        mTvIssue.text = data.openIssuesCount.toString()
        mTvFork.text = data.forksCount.toString()
    }

    private fun getLanguageColor(language: String?): Drawable {
        if (language == null) return ColorDrawable(Color.TRANSPARENT)

        val colorProvider: (Int) -> Drawable = { resId ->
            ColorDrawable(ContextCompat.getColor(mIvLanguageColor.context, resId))
        }

        return colorProvider(
            when (language) {
                "Kotlin" -> R.color.color_language_kotlin
                "Java" -> R.color.color_language_java
                "JavaScript" -> R.color.color_language_js
                "Python" -> R.color.color_language_python
                "HTML" -> R.color.color_language_html
                "CSS" -> R.color.color_language_css
                "Shell" -> R.color.color_language_shell
                "C++" -> R.color.color_language_cplus
                "C" -> R.color.color_language_c
                "ruby" -> R.color.color_language_ruby
                else -> R.color.color_language_other
            }
        )
    }

    companion object {

        fun create(parent: ViewGroup): ReposPagedListViewHolder =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_repos_repo, parent, false)
                .run(::ReposPagedListViewHolder)
    }
}

sealed class RepoPagedListItemEvent {

    data class ClickEvent(val url: String) : RepoPagedListItemEvent()
}