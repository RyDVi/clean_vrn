package com.rydvi.clean_vrn.ui.error

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Error

class ErrorHandler(activity: Activity) {

    private val activity: Activity = activity

    fun showCustomError(msg: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setMessage(msg)
            .setPositiveButton(activity.resources.getString(R.string.dlg_error_ok)) { _, _ ->
            }
        activity.runOnUiThread {
            builder.create().show()
        }
    }

    fun showError(error: Error) {
        val resources = activity.resources
        var msg = ""
        error.msg?.let {errorMsg->
            msg = resources.getString(R.string.dlg_error_msg, errorMsg)
        }
        error.code?.let {errorCode->
            msg = "$msg\n${resources.getString(R.string.dlg_error_code, errorCode)}"
        }
        showCustomError(msg)
    }
}