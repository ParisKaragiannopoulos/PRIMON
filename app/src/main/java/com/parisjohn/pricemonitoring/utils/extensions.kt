package com.parisjohn.pricemonitoring.utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import java.io.IOException

import java.io.InputStream





fun Context.showToast(
    msg: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, msg, duration).show()


inline fun String.ifNotEmpty(defaultValue: (Any?) -> String): String =
    if (isNotEmpty()) defaultValue(this) else this

fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}