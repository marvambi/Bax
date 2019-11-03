package com.marvambi.degrande.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure
import android.widget.Toast



@SuppressLint("HardwareIds")
fun Context.getAndroidId(): String = Secure.getString(this.contentResolver, Secure.ANDROID_ID)

fun Context.showToast(message: String) = Toast.makeText(this,  message, Toast.LENGTH_LONG).show()