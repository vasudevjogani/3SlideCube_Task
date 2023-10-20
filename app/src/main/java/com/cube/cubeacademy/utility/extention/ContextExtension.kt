package com.cube.cubeacademy.utility.extention

import android.content.Context
import android.widget.Toast
import com.cube.cubeacademy.R

fun Context.showErrorToast() {
    Toast.makeText(this, this.getString(R.string.something_went_wrong_please_try_again_later), Toast.LENGTH_LONG).show()
}

fun Context.showNoInternetToast() {
    Toast.makeText(this, this.getString(R.string.no_internet_connection_please_try_again_later), Toast.LENGTH_LONG).show()
}

