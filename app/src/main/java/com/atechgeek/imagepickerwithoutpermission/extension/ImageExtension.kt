package com.atechgeek.imagepickerwithoutpermission.extension

import android.widget.ImageView
import com.atechgeek.imagepickerwithoutpermission.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


fun ImageView.loadCircularImageWithoutCache(url: String?) {
    Glide.with(this.context)
        .asBitmap()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .placeholder(R.mipmap.ic_launcher_round)
        .into(this)
}