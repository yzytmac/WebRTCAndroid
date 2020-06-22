package com.yzytmac.webrtcandroid.common

import android.util.Log
import com.yzytmac.webrtcandroid.App
import com.yzytmac.webrtcandroid.activity.CallActivity
import com.yzytmac.webrtcandroid.activity.ChatActivity
import com.yzytmac.webrtcandroid.model.Message
import okhttp3.WebSocket
import org.webrtc.SurfaceViewRenderer

/**
 * Created by yzy on 2020-06-20 15:48
 * Email: yzytmac@163.com
 * Phone: 18971165201
 * QQ: 398564331
 * Description:
 */
object IMManager {
    private var user: String = ""
    private var webSocket: WebSocket? = null

    /**
     * 与对方开始互联
     */
    fun init(toUser: String, localVideoView: SurfaceViewRenderer, remoteVideoView: SurfaceViewRenderer) {
        WebRtcHelper.init(App.context, localVideoView, remoteVideoView) {
            Log.e("yzy", "IMManager->init->第30行:发送candidate")
            webSocket?.send(Message(user, toUser, EVENT_CANDIDATE, iceCandidate = it).toJsonString())
        }
    }

    /**
     * 当websocket连接
     */
    fun onWebSocketOpen(user: String, webSocket: WebSocket) {
        IMManager.user = user
        IMManager.webSocket = webSocket
        ChatActivity.start(App.context)
    }

    /**
     * 当连接失败或断开连接
     */
    fun onWebSocketClosed() {

    }

    /**
     * 呼叫
     */
    fun call(toUser: String) {

        //发送call信令
        Log.e("yzy", "IMManager->call->第57行:发送call")
        webSocket?.send(Message(user, toUser, EVENT_CALL).toJsonString())
    }

    /**
     * 被呼叫时
     */
    fun onCall(message: Message) {
        CallActivity.start(App.context, message.from_user, false)//被动呼叫
    }

    /**
     * 接听
     */
    fun listen(toUser: String) {
        //发送listen信令
        Log.e("yzy", "IMManager->listen->第73行:发送listen")
        webSocket?.send(Message(user, toUser, EVENT_LISTEN).toJsonString())
    }

    /**
     * 对方接听时
     */
    fun onListen(message: Message) {
        //要刷新ui

        //offer操作
        offer(message.from_user)
    }

    /**
     * 创建offer并发送
     */
    fun offer(toUser: String) {
        WebRtcHelper.createOffer() {
            Log.e("yzy", "IMManager->offer->第92行:发送offer")
            webSocket?.send(Message(user, toUser, EVENT_OFFER, it).toJsonString())
        }
    }

    /**
     * 收到对方offer时
     */
    fun onOffer(message: Message) {
        message.sdp?.let {
            WebRtcHelper.setRemoteDescription(it)
        }
        answer(message.from_user)
    }

    /**
     * 创建answer并发送
     */
    fun answer(toUser: String) {
        Log.e("yzy", "IMManager->answer->第106行:")
        WebRtcHelper.createAnswer {
            Log.e("yzy", "IMManager->answer->第109行:发送answer")
            webSocket?.send(Message(user, toUser, EVENT_ANSWER, it).toJsonString())
        }
    }

    /**
     * 收到对方answer时
     */
    fun onAnswer(message: Message) {
        message.sdp?.let {
            WebRtcHelper.setRemoteDescription(it)
        }
    }

    /**
     * 当收到对方的IceCandidate时
     */
    fun onIceCandidate(message: Message) {
        message.iceCandidate?.let {
            WebRtcHelper.addIceCandidate(it)
        }
    }

    /**
     * 拒接挂断
     */
    fun refuse(toUser: String) {
        //发送refuse信令
        Log.e("yzy", "IMManager->refuse->第152行:发送挂断")
        webSocket?.send(Message(user, toUser, EVENT_REFUSE).toJsonString())
    }

    /**
     * 对方挂断拒绝时
     */
    fun onRefuse(message: Message) {}

    /**
     * 对方发来的其他消息
     */
    fun onOther(message: Message) {}

}