package com.github.qingmei2.sample.entity

sealed class Errors : Throwable() {

    object NetworkError : Errors()

    object EmptyInputError : Errors()

    object EmptyResultsError : Errors()

    data class SimpleError(val simpleMessage: String) : Errors()

    object SingleError : Errors()
}