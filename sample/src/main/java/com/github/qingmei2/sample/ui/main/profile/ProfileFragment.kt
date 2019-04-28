package com.github.qingmei2.sample.ui.main.profile

import android.os.Bundle
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.github.qingmei2.mvi.base.view.fragment.BaseFragment
import com.github.qingmei2.mvi.ext.reactivex.throttleFirstClicks
import com.github.qingmei2.mvi.image.GlideApp
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.UserInfo
import com.github.qingmei2.sample.utils.toast
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_profile.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class ProfileFragment : BaseFragment<ProfileIntent, ProfileViewState>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(profileKodeinModule)
    }

    override val layoutId: Int = R.layout.fragment_profile

    private val mViewModel: ProfileViewModel by instance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binds()
    }

    private fun binds() {
        mViewModel.states()
            .autoDisposable(scopeProvider)
            .subscribe(this::render)
        mBtnEdit.throttleFirstClicks()
            .autoDisposable(scopeProvider)
            .subscribe { toast("Coming soon...") }

        mViewModel.processIntents(intents())
    }

    override fun intents(): Observable<ProfileIntent> {
        return initialIntent()
    }

    private fun initialIntent(): Observable<ProfileIntent> {
        return Observable.just(ProfileIntent.InitialIntent)
    }

    override fun render(state: ProfileViewState) {
        state.error?.apply {
            when (this) {
                is Errors.SimpleMessageError -> toast(simpleMessage)
                is Errors.ErrorWrapper -> {
                    toast("error:${errors.localizedMessage}")
                }
                else -> {
                    toast("error:$localizedMessage")
                }
            }
        }

        val event = state.uiEvent
        when (event) {
            is ProfileUIEvent.InitialSuccess -> {
                onInitialResult(event.user)
            }
        }
    }

    private fun onInitialResult(user: UserInfo) {
        GlideApp.with(context!!)
            .load(user.avatarUrl)
            .apply(RequestOptions().circleCrop())
            .into(mIvAvatar)
        mTvNickname.text = user.name
        mTvBio.text = user.bio
        mTvLocation.text = user.location
    }
}