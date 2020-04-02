package com.rydvi.clean_vrn.ui.utils

import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository

enum class UserType {
    administrator {
        override fun getUserTypeId(): Long = 1
        override fun getUserTypeString() = R.string.user_type_administrator
    },
    organizator {
        override fun getUserTypeId(): Long = 2
        override fun getUserTypeString() = R.string.user_type_organizator
    },
    player {
        override fun getUserTypeId(): Long = 3
        override fun getUserTypeString() = R.string.user_type_player
    };

    abstract fun getUserTypeId(): Long
    abstract fun getUserTypeString(): Int
}

fun getUserTypeByTypeId(idType: Long): UserType? {
    return when (idType) {
        UserType.administrator.getUserTypeId() -> UserType.administrator
        UserType.organizator.getUserTypeId() -> UserType.organizator
        UserType.player.getUserTypeId() -> UserType.player
        else -> null
    }
}

fun getUserType(): UserType? {
    DataRepository.getSession()?.let {session ->
        session.idUserType?.let { idUserType->
            return getUserTypeByTypeId(idUserType)
        }.also {
            return null
        }
    }.also {
        return null
    }

}

fun isAdmin(): Boolean = getUserType() === UserType.administrator

fun isOrganizator(): Boolean = getUserType() === UserType.organizator

fun isPlayer(): Boolean = getUserType() === UserType.player