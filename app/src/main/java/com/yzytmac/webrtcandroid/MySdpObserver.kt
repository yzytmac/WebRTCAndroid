package com.yzytmac.webrtcandroid

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

/**
 * Created by yzy on 2020-06-18 09:28
 * Email: yzytmac@163.com
 * Phone: 18971165201
 * QQ: 398564331
 * Description:
 */
open class MySdpObserver:SdpObserver {
    override fun onSetFailure(p0: String?) {
        Log.d("yzy", "MySdpObserver->onSetFailure->第15行:")
    }

    override fun onSetSuccess() {
        Log.d("yzy", "MySdpObserver->onSetSuccess->第20行:")
    }

    override fun onCreateSuccess(sdp: SessionDescription?) {
        Log.d("yzy", "MySdpObserver->onCreateSuccess->第24行:")
    }

    override fun onCreateFailure(p0: String?) {
        Log.e("yzy", "MySdpObserver->onCreateFailure->第28行:$p0")
    }
}