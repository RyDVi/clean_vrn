package com.rydvi.clean_vrn.ui.utils

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