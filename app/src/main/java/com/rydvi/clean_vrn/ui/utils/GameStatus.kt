package com.rydvi.clean_vrn.ui.utils

import android.content.Context
import android.os.Build
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

fun getColorByGameStatus(context: Context, idStatus: Long) = when (idStatus) {
    GameStatus.started.getTypeId() -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.resources.getColor(R.color.colorTextGameStatusStarted, null)
    } else {
        context.resources.getColor(R.color.colorTextGameStatusStarted)
    }
    GameStatus.completed.getTypeId() -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.resources.getColor(R.color.colorTextGameStatusEnded, null)
    } else {
        context.resources.getColor(R.color.colorTextGameStatusEnded)
    }
    GameStatus.planned.getTypeId() -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.resources.getColor(R.color.colorTextGameStatusPlanned, null)
    } else {
        context.resources.getColor(R.color.colorTextGameStatusPlanned)
    }
    else -> null
}

fun getTextByGameStatus(context:Context, idStatus: Long) = when (idStatus){
    GameStatus.started.getTypeId()-> context.resources.getString(R.string.txt_game_status_started)
    GameStatus.completed.getTypeId()-> context.resources.getString(R.string.txt_game_status_completed)
    GameStatus.planned.getTypeId()-> context.resources.getString(R.string.txt_game_status_planned)
    else -> null
}
