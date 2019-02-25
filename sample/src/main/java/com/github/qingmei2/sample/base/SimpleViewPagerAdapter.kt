package com.github.qingmei2.sample.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SimpleViewPagerAdapter(
        fragmentManager: FragmentManager,
        private val fragments: List<Fragment>
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(index: Int): Fragment = fragments[index]

    override fun getCount(): Int = fragments.size
}