package com.github.qingmei2.sample.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.github.qingmei2.mvi.base.view.fragment.AutoDisposeFragment
import com.github.qingmei2.sample.R
import com.github.qingmei2.sample.base.SimpleViewPagerAdapter
import com.github.qingmei2.sample.ui.main.home.HomeFragment
import com.jakewharton.rxbinding3.material.itemSelections
import com.jakewharton.rxbinding3.viewpager.pageSelections
import com.uber.autodispose.autoDisposable
import kotlinx.android.synthetic.main.fragment_main.*

@SuppressLint("CheckResult")
class MainFragment : AutoDisposeFragment() {

    private lateinit var mViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = MainViewModel.instance(this)

        initViewPager()

        binds()
    }

    private fun initViewPager() {
        val fragments = listOf(HomeFragment(), HomeFragment(), HomeFragment())

        mViewPager.adapter = SimpleViewPagerAdapter(childFragmentManager, fragments)
        mViewPager.currentItem = 0
        mViewPager.offscreenPageLimit = 2
    }

    private fun binds() {
        mBottomNavigation.itemSelections()
                .autoDisposable(scopeProvider)
                .subscribe(::onBottomNavigationSelectChanged)
        mViewPager.pageSelections()
                .map { it }
                .autoDisposable(scopeProvider)
                .subscribe(::onPageSelectChangedPort)
    }

    private fun onPageSelectChangedPort(index: Int) {
        for (position in 0..index) {
            if (mBottomNavigation.visibility == View.VISIBLE)
                mBottomNavigation.menu.getItem(position).isChecked = index == position
        }
    }

    private fun onBottomNavigationSelectChanged(menuItem: MenuItem) {
        mViewPager.currentItem = when (menuItem.itemId) {
            R.id.nav_home -> 0
            R.id.nav_repos -> 1
            R.id.nav_profile -> 2
            else -> throw IllegalArgumentException("Wrong menuItem param.")
        }
    }
}