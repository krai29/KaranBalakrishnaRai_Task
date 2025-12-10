package com.krai29.karanbalakrishnaraitask.core

sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Error(val throwable: Throwable) : DomainResult<Nothing>()
    object Loading : DomainResult<Nothing>()
}