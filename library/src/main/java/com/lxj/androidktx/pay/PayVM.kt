package com.lxj.androidktx.pay

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.alipay.sdk.app.PayTask
import com.lxj.androidktx.livedata.NoStickyLiveData
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlin.concurrent.thread

/**
 * Description:
 * Create by dance, at 2019/6/28
 */
object PayVM : ViewModel() {

    var wechatAppId = ""
    fun init(wxAppId: String){
        wechatAppId = wxAppId
    }

    val aliPayData = NoStickyLiveData<AliPayResult>()
    fun aliPay(orderParam: String, activity: Activity) {
        thread(start = true) {
            val result = PayTask(activity).payV2(orderParam, true)
            aliPayData.postValue(AliPayResult.fromMap(result))
        }
    }

    val wxPayData = MutableLiveData<Any>()
    fun wxPay(
        context: Context, appid: String, partnerId: String, prepayId: String,
        nonceStr: String, timeStamp: String, packageValue: String = "Sign=WXPay",
        sign: String, extData: String = ""
    ) {
        val wxapi = WXAPIFactory.createWXAPI(context, wechatAppId)
        val req = PayReq()
        req.appId = appid
        req.partnerId = partnerId
        req.prepayId = prepayId
        req.nonceStr = nonceStr
        req.timeStamp = timeStamp
        req.packageValue = packageValue
        req.sign = sign
        req.extData = extData // optional
        wxapi.sendReq(req)
    }
}