package com.lxj.androidktx.core


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.*
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import com.lxj.androidktx.livedata.LifecycleHandler
import java.io.Serializable
import java.util.*


/**
 * Description:  通用扩展
 * Create by dance, at 2018/12/5
 */

/** dp和px转换 **/
inline val Float.dp
    get() = ConvertUtils.dp2px(this)
inline val Float.sp
    get() = ConvertUtils.sp2px(this)
inline val Float.pt
    get() = AdaptScreenUtils.pt2Px(this)
inline val Int.dp
    get() = ConvertUtils.dp2px(this.toFloat())
inline val Int.sp
    get() = ConvertUtils.sp2px(this.toFloat())
inline val Int.pt
    get() = AdaptScreenUtils.pt2Px(this.toFloat())

fun Context.dp2px(dpValue: Float): Int {
    return ConvertUtils.dp2px(dpValue)
}

fun Context.sp2px(spValue: Float): Int {
    return ConvertUtils.sp2px(spValue)
}

fun Fragment.dp2px(dpValue: Float): Int {
    return context!!.dp2px(dpValue)
}

fun Fragment.sp2px(dpValue: Float): Int {
    return context!!.sp2px(dpValue)
}

fun View.dp2px(dpValue: Float): Int {
    return context!!.dp2px(dpValue)
}
fun View.sp2px(dpValue: Float): Int {
    return context!!.sp2px(dpValue)
}

/** 动态创建Drawable
 * @param cornerRadiusArray [lt, lr, br, bl] ，长度必须为4
 */
fun Context.createDrawable(color: Int = Color.TRANSPARENT, radius: Float = 0f, cornerRadiusArray: Array<Float>? = null,
                           strokeColor: Int = Color.TRANSPARENT, strokeWidth: Int = 0,
                           enableRipple: Boolean = true,
                           rippleColor: Int = Color.parseColor("#88999999"),
                           gradientStartColor: Int = 0, gradientCenterColor : Int = 0, gradientEndColor : Int = 0,
                           gradientOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT,
                           shadowColor: Int? = null, shadowSize: Float? = null): Drawable {
    var d: Drawable = GradientDrawable().apply {
        cornerRadius = radius
        if(cornerRadiusArray!=null){
            val arr = FloatArray(8)
            arr[0] = cornerRadiusArray[0]
            arr[1] = cornerRadiusArray[0]
            arr[2] = cornerRadiusArray[1]
            arr[3] = cornerRadiusArray[1]
            arr[4] = cornerRadiusArray[2]
            arr[5] = cornerRadiusArray[2]
            arr[6] = cornerRadiusArray[3]
            arr[7] = cornerRadiusArray[3]
            cornerRadii = arr
        }
        setStroke(strokeWidth, strokeColor)
        gradientType = GradientDrawable.LINEAR_GRADIENT
        if(gradientStartColor!=0 || gradientEndColor!=0 ){
            orientation = gradientOrientation
            colors = if(gradientCenterColor!=0) intArrayOf(gradientStartColor, gradientCenterColor, gradientEndColor)
            else  intArrayOf(gradientStartColor, gradientEndColor)
        }else{
            setColor(color)
        }
    }
    if (Build.VERSION.SDK_INT >= 21 && enableRipple) {
        return RippleDrawable(ColorStateList.valueOf(rippleColor), d, null)
    }
//    if((shadowColor!=null && shadowColor!=0) || shadowSize?:0f > 0f){
//        d = ShadowDrawable(d, shadowColor ?: 0 , radius ,shadowSize ?: 0f, shadowSize?: 0f)
//    }
    return d
}

fun Fragment.createDrawable(color: Int = Color.TRANSPARENT, radius: Float = 0f, cornerRadiusArray: Array<Float>? = null,
                            strokeColor: Int = Color.TRANSPARENT, strokeWidth: Int = 0,
                            enableRipple: Boolean = true,
                            rippleColor: Int = Color.parseColor("#88999999"),
                            gradientStartColor: Int = 0, gradientCenterColor : Int = 0,gradientEndColor : Int = 0,
                            gradientOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT,
                            shadowColor: Int? = null, shadowSize: Float? = null): Drawable {
    return context!!.createDrawable(color, radius, cornerRadiusArray, strokeColor, strokeWidth, enableRipple, rippleColor, gradientStartColor = gradientStartColor,
    gradientEndColor = gradientEndColor, gradientOrientation = gradientOrientation, gradientCenterColor = gradientCenterColor,
        shadowColor = shadowColor, shadowSize = shadowSize)
}

fun View.createDrawable(color: Int = Color.TRANSPARENT, radius: Float = 0f, cornerRadiusArray: Array<Float>? = null,
                        strokeColor: Int = Color.TRANSPARENT, strokeWidth: Int = 0,
                        enableRipple: Boolean = true,
                        rippleColor: Int = Color.parseColor("#88999999"),
                        gradientStartColor: Int = 0, gradientCenterColor : Int = 0, gradientEndColor : Int = 0,
                        gradientOrientation: GradientDrawable.Orientation = GradientDrawable.Orientation.LEFT_RIGHT,
                        shadowColor: Int? = null, shadowSize: Float? = null): Drawable {
    return context!!.createDrawable(color, radius, cornerRadiusArray, strokeColor, strokeWidth, enableRipple, rippleColor, gradientStartColor = gradientStartColor,
            gradientEndColor = gradientEndColor, gradientOrientation = gradientOrientation, gradientCenterColor = gradientCenterColor,
        shadowColor = shadowColor, shadowSize = shadowSize)
}

/** json相关 **/
fun Any.toJson(dateFormat: String = "yyyy-MM-dd HH:mm:ss", lenient: Boolean = false, excludeFields: List<String>? = null)
        = GsonBuilder().setDateFormat(dateFormat)
        .setObjectToNumberStrategy(ToNumberPolicy.BIG_DECIMAL)
        .apply {
            if(lenient) setLenient()
            if(!excludeFields.isNullOrEmpty()){
                setExclusionStrategies(object : ExclusionStrategy{
                    override fun shouldSkipField(f: FieldAttributes?): Boolean {
                        return f!=null && excludeFields.contains(f.name)
                    }
                    override fun shouldSkipClass(clazz: Class<*>?) = false
                })
            }
        }
        .create().toJson(this)

inline fun <reified T> String.toBean(dateFormat: String = "yyyy-MM-dd HH:mm:ss", lenient: Boolean = false)
        = GsonBuilder().setDateFormat(dateFormat)
        .setObjectToNumberStrategy(ToNumberPolicy.BIG_DECIMAL)
        .apply {
            if(lenient) setLenient()
        }.create()
        .fromJson<T>(this, object : TypeToken<T>() {}.type)

inline fun <reified T> Any.deepCopy(): T = toJson().toBean<T>()

/**
 * 数组转bundle
 */
fun Array<out Pair<String, Any?>>.toBundle(): Bundle? {
    return Bundle().apply {
        forEach { it ->
            val value = it.second
            when (value) {
                null -> putSerializable(it.first, null as Serializable?)
                is Int -> putInt(it.first, value)
                is Long -> putLong(it.first, value)
                is CharSequence -> putCharSequence(it.first, value)
                is String -> putString(it.first, value)
                is Float -> putFloat(it.first, value)
                is Double -> putDouble(it.first, value)
                is Char -> putChar(it.first, value)
                is Short -> putShort(it.first, value)
                is Boolean -> putBoolean(it.first, value)
                is Serializable -> putSerializable(it.first, value)
                is Parcelable -> putParcelable(it.first, value)

                is IntArray -> putIntArray(it.first, value)
                is LongArray -> putLongArray(it.first, value)
                is FloatArray -> putFloatArray(it.first, value)
                is DoubleArray -> putDoubleArray(it.first, value)
                is CharArray -> putCharArray(it.first, value)
                is ShortArray -> putShortArray(it.first, value)
                is BooleanArray -> putBooleanArray(it.first, value)

                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> putCharSequenceArray(it.first, value as Array<CharSequence>)
                    value.isArrayOf<String>() -> putStringArray(it.first, value as Array<String>)
                    value.isArrayOf<Parcelable>() -> putParcelableArray(it.first, value as Array<Parcelable>)
                }
            }
        }
    }

}


fun Any.runOnUIThread(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post { action() }
}

//一天只做一次
fun Any.doOnceInDay(actionName: String = "", keySuffix: String? = "", action: () -> Unit, whenHasDone: (()->Unit)? = null) {
    val key = "once_in_day_last_check_${actionName}_${keySuffix}"
    val today = Date()
    val todayFormat = TimeUtils.date2String(today, "yyyy-MM-dd")
    val last = sp().getString(key, "")
    if (last != null && last.isNotEmpty() && last == todayFormat) {
        //说明执行过
        whenHasDone?.invoke()
        return
    }
    sp().putString(key, todayFormat)
    action()
}

//只执行一次的行为
fun Any.doOnlyOnce(actionName: String = "", keySuffix: String? = "", action: () -> Unit, whenHasDone: (()->Unit)? = null) {
    val key = "has_done_${actionName}_${keySuffix}"
    val hasDone = sp().getBoolean(key, false)
    if (hasDone) {
        //说明执行过
        whenHasDone?.invoke()
        return
    }
    sp().putBoolean(key, true)
    action()
}

//500毫秒内只做一次
val _actionCache = arrayListOf<String>()
val _doOnceInHandler = Handler(Looper.getMainLooper())
/**
 * 事件节流，在指定时间内只执行一次事件；注意事项，内部共享Handler，并发调用的情况可能会有问题。
 * @param actionName 事件的名字
 * @param time 事件的节流时间
 * @param immediately 是否立即执行，true则立即执行任务，在time时间内不再接收新的任务：
 *                      false是延时time后执行任务，在time时间内新的任务会替换之前的任务
 * @param action 事件
 */
fun Any.doOnceIn( actionName: String, time: Long = 800, immediately: Boolean = true, action: ()->Unit){
    if(immediately) {
        //立即执行任务，在time时间内不再接收新的任务
        if(_actionCache.contains(actionName)) return
        _actionCache.add(actionName)
        action() //立即执行
        _doOnceInHandler.postDelayed({
            if(_actionCache.contains(actionName)) _actionCache.remove(actionName)
        }, time)
    }else{
        //在time时间内新的任务会替换之前的任务
        _doOnceInHandler.removeCallbacksAndMessages(null)
        _doOnceInHandler.postDelayed({
            action() //立即执行
        }, time)
    }
}

var _doOnceInLifecycleHandler: LifecycleHandler? = null
/**
 * 事件节流，在指定时间内只执行一次事件；注意事项，内部共享Handler，并发调用的情况可能会有问题。
 * @param actionName 事件的名字
 * @param time 事件的节流时间
 * @param immediately 是否立即执行，true则立即执行任务，在time时间内不再接收新的任务：
 *                      false是延时time后执行任务，在time时间内新的任务会替换之前的任务
 * @param action 事件
 */
fun Any.doOnceIn(lifecycleOwner: LifecycleOwner,  actionName: String, time: Long = 800, immediately: Boolean = true, action: ()->Unit){
    if(_doOnceInLifecycleHandler==null) _doOnceInLifecycleHandler = LifecycleHandler(lifecycleOwner)
    if(immediately) {
        //立即执行任务，在time时间内不再接收新的任务
        if(_actionCache.contains(actionName)) return
        _actionCache.add(actionName)
        action() //立即执行
        _doOnceInLifecycleHandler?.postDelayed({
            if(_actionCache.contains(actionName)) _actionCache.remove(actionName)
        }, time)
    }else{
        //在time时间内新的任务会替换之前的任务
        _doOnceInLifecycleHandler?.removeCallbacksAndMessages(null)
        _doOnceInLifecycleHandler?.postDelayed({
            action() //立即执行
        }, time)
    }
}

fun Any.context2Activity(context: Context): Activity?{
    var ctx = context
    while (ctx is ContextWrapper) {
        if (ctx is Activity) {
            return ctx
        } else {
            ctx = (ctx as ContextWrapper).baseContext
        }
    }
    return null
}