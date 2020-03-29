package com.rydvi.clean_vrn.ui.utils

import com.rydvi.clean_vrn.R

enum class UserType {
    administrator {
        override fun getUserTypeId() = 1
        override fun getUserTypeString() = R.string.user_type_administrator
    },
    organizator {
        override fun getUserTypeId() = 2
        override fun getUserTypeString() = R.string.user_type_organizator
    },
    player{
        override fun getUserTypeId() = 3
        override fun getUserTypeString() = R.string.user_type_player
    };
    abstract fun getUserTypeId(): Int
    abstract fun getUserTypeString():Int
}