package com.marvambi.degrande.common.option

import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.marvambi.degrande.R


fun defaultOptions(
        placeHolder: Int = R.mipmap.ic_launcher,
        error: Int = R.mipmap.ic_launcher
): RequestOptions = requestOptions(RequestOptions(), placeHolder, error)

fun centerCropOptions(
        placeHolder: Int = R.mipmap.ic_launcher,
        error: Int = R.mipmap.ic_launcher
): RequestOptions = requestOptions(circleCropTransform(), placeHolder, error)

private fun requestOptions(options: RequestOptions, placeHolder: Int, error: Int) =
        options.placeholder(placeHolder).error(error)

