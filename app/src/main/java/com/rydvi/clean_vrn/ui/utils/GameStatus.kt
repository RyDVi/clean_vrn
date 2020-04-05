package com.rydvi.clean_vrn.ui.utils

import com.rydvi.clean_vrn.R

enum class GameStatus {
    started {
        override fun getTypeId(): Long = 1
    },
    completed {
        override fun getTypeId(): Long = 2
    },
    planned {
        override fun getTypeId(): Long = 3
    }, ;

    abstract fun getTypeId(): Long
}