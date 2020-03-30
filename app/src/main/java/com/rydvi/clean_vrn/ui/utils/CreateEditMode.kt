package com.rydvi.clean_vrn.ui.utils

import com.rydvi.clean_vrn.ui.games.GameCreateEditFragment

enum class CreateEditMode {
    EDIT{
        override fun getMode(): String = "EDIT"
    },
    CREATE{
        override fun getMode(): String = "CREATE"
    },
    READ{
        override fun getMode(): String = "READ"
    };
    abstract fun getMode(): String
}


fun getCreateEditModeByString(createEditMode:String):CreateEditMode{
    var editMode: CreateEditMode? = null
    when (createEditMode){
        CreateEditMode.EDIT.getMode() -> editMode = CreateEditMode.EDIT
        CreateEditMode.CREATE.getMode() -> editMode = CreateEditMode.CREATE
        CreateEditMode.READ.getMode() -> editMode = CreateEditMode.EDIT
    }
    return editMode!!
}