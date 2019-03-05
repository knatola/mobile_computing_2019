package com.example.whatisup.src.data.common

data class State(var status: Status, var error: Throwable? = null)

enum class Status { SUCCESSFUL, ERROR, LOADING}