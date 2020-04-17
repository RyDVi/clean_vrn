package com.rydvi.clean_vrn.ui.login

import android.app.Activity
import android.content.Context.MODE_PRIVATE

data class UserData(
    val activity: Activity,
    var username: String? = null,
    var password: String? = null
) {
    /**
     * Сохранение учетных данных. Если сохранено, то возвращает true, иначе false
     * Сохраняет именно в тот activity, который указан в activity
     */
    fun save(): Boolean {
        return if (!username.isNullOrEmpty() && !username.isNullOrEmpty()) {
            val preferences = activity.getPreferences(MODE_PRIVATE)
            val editor = preferences.edit()
            //Запись значений
            editor.putString(USER_DATA_STORE_USERNAME, username)
            editor.putString(USER_DATA_STORE_PASSWORD, password)
            //Сохранение значений
            editor.commit()
            true
        } else {
            false
        }
    }

    /**
     * Загрузка учетных данных из указанного activity
     */
    fun load(): UserData {
        val preferences = activity.getPreferences(MODE_PRIVATE)
        username = preferences.getString(USER_DATA_STORE_USERNAME, null)
        password = preferences.getString(USER_DATA_STORE_PASSWORD, null)
        return this
    }


    companion object {
        /**
         * Очищает учетные данные из указанного activity
         */
        fun clear(activity: Activity){
            val preferences = activity.getPreferences(MODE_PRIVATE)
            val editor = preferences.edit()
            editor.remove(USER_DATA_STORE_USERNAME)
            editor.remove(USER_DATA_STORE_PASSWORD)
            editor.apply()
        }
        const val USER_DATA_STORE_USERNAME = "user_data_store_username"
        const val USER_DATA_STORE_PASSWORD = "user_data_store_password"
    }
}