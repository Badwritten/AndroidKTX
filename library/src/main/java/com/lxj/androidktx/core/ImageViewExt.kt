package com.lxj.androidktx.core

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

/**
 * Description: ImageView相关
 * Create by lxj, at 2018/12/5
 */

/**
 * Glide加载图片
 * @param url 可以是网络，可以是File，可以是资源id等等Glide支持的类型
 * @param placeholder 默认占位图
 * @param error 失败占位图
 * @param isCircle 是否是圆形，默认false，注意：isCircle和roundRadius两个只能有一个生效
 * @param roundRadius 圆角角度，默认为0，不带圆角，注意：isCircle和roundRadius两个只能有一个生效
 * @param isCrossFade 是否有过渡动画，默认没有过渡动画
 */
fun ImageView.load(url: Any, placeholder: Int = 0, error: Int = 0,
                   isCircle: Boolean = false,
                   roundRadius: Int = 0,
                   isCrossFade: Boolean = false) {
    val options = RequestOptions().placeholder(placeholder).error(error).apply {
        if (isCircle) {
            circleCrop()
        } else if (roundRadius != 0) {
            if (scaleType == ImageView.ScaleType.CENTER_CROP) {
                transforms(CenterCrop(), RoundedCorners(roundRadius))
            } else {
                transform(RoundedCorners(roundRadius))
            }
        }
    }
    Glide.with(context).load(url)
            .apply(options)
            .apply {
                if (isCrossFade) transition(DrawableTransitionOptions.withCrossFade())
            }
            .into(this)
}