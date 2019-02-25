package com.github.qingmei2.sample.utils

import android.widget.Toast
import com.github.qingmei2.sample.base.BaseApplication

fun toast(value: String) = toast { value }

inline fun toast(value: () -> String) =
        Toast.makeText(BaseApplication.INSTANCE, value(), Toast.LENGTH_SHORT).show()

