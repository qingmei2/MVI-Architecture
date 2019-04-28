package com.github.qingmei2.sample.entity.event

data class AutoLoginEvent(
    val autoLogin: Boolean,
    val username: String,
    val password: String
)