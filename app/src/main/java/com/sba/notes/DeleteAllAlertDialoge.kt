package com.sba.notes

import android.app.Activity
import android.app.AlertDialog

class DeleteAllAlertDialoge(val activity: Activity) {
    lateinit var dialog: AlertDialog

    fun startDeleteDialoge()
    {
        val builder =AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setView(R.layout.alert_dialog_delete_all)
        builder.setCancelable(true)

        dialog =builder.create()
        dialog.show()
    }
    fun dismisDeleteDialog()
    {
        dialog.dismiss()
    }
}