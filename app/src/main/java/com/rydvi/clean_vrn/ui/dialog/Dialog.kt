package com.rydvi.clean_vrn.ui.dialog

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AlertDialog

class Dialog(activity: Activity) {
    val activity: Activity = activity

    fun showDialogAcceptCancel(
        callbackAccept: () -> Unit,
        callbackCancel: (() -> Unit)?,
        text: String,
        textBtnAccept: String,
        textBtnCancel: String
    ) {
        val builderAccept: AlertDialog.Builder =
            AlertDialog.Builder(activity)
        builderAccept.setMessage(text)
            .setPositiveButton(textBtnAccept) { _, _ ->
                callbackAccept()
            }
            .setNegativeButton(textBtnCancel) { _, _ ->
                callbackCancel?.let { it() }
            }
        activity.runOnUiThread {
            builderAccept.create().show()
        }
    }

    fun showDialogAccept(callbackAccept: () -> Unit, text: String, textBtnAccept: String) {
        val builderAccept: AlertDialog.Builder =
            AlertDialog.Builder(activity)
        builderAccept.setMessage(text)
            .setPositiveButton(textBtnAccept) { _, _ ->
                callbackAccept()
            }
        activity.runOnUiThread {
            builderAccept.create().show()
        }
    }

    fun showDialogAcceptCancelWithContent(
        callbackAccept: () -> Unit,
        callbackCancel: (() -> Unit)?,
        title: String,
        textBtnAccept: String,
        textBtnCancel: String,
        content: View
    ) {
        val builderAccept: AlertDialog.Builder =
            AlertDialog.Builder(activity)

        builderAccept
            .setView(content)
            .setTitle(title)
            .setPositiveButton(textBtnAccept) { _, _ ->
                callbackAccept()
            }
            .setNegativeButton(textBtnCancel) { _, _ ->
                callbackCancel?.let { it() }
            }
        activity.runOnUiThread {
            builderAccept.create().show()
        }
    }
}