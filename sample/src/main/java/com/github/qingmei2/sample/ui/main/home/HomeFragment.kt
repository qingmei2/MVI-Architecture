package com.github.qingmei2.sample.ui.main.home

import androidx.paging.PagedList
import com.github.qingmei2.mvi.base.view.fragment.BaseFragment
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.entity.Errors
import com.github.qingmei2.sample.entity.ReceivedEvent
import com.github.qingmei2.sample.utils.toast
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class HomeFragment : BaseFragment<HomeIntent, HomeViewState>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(homeKodeinModule)
    }

    override val layoutId: Int = R.layout.fragment_home

    private val mViewModel: HomeViewModel by instance()

    override fun onStart() {
        super.onStart()

        binds()
    }

    private fun binds() {
        mViewModel.states()
                .autoDisposable(scopeProvider)
                .subscribe(this::render)

        mViewModel.processIntents(intents())
    }

    override fun intents(): Observable<HomeIntent> {
        return initialIntent()
//        return Observable.merge(
//                initialIntent(),
//                initialIntent()
//        )
    }

    private fun initialIntent(): Observable<HomeIntent> =
            Observable.just(HomeIntent.InitialIntent)

    override fun render(state: HomeViewState) {
        state.error?.apply {
            when (this) {
                is Errors -> when (this) {
                    is Errors.SimpleMessageError -> {
                        toast(simpleMessage)
                    }
                    is Errors.ErrorWrapper -> {
                        toast("error:${errors.localizedMessage}")
                    }
                    else -> {
                        toast("error:$localizedMessage")
                    }
                }
            }
        }

        mSwipeRefreshLayout.isRefreshing = state.isRefreshing

        state.uiEvent.apply {
            when (this) {
                is HomeViewState.HomeUIEvent.InitialSuccess -> {
                    initPagedListAdapter(this.pageList)
                }
            }
        }
    }

    private fun initPagedListAdapter(pageList: PagedList<ReceivedEvent>) {
        val adapter = HomePagedListAdapter()
        mRecyclerView.adapter = adapter
        adapter.submitList(pageList)
    }
}