package com.github.qingmei2.sample.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.github.qingmei2.sample.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(navHostFragment).navigateUp()

    companion object {

        fun launch(activity: FragmentActivity) =
            activity.startActivity(Intent(activity, MainActivity::class.java))
    }
}