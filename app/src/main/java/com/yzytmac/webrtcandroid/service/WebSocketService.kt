package com.yzytmac.webrtcandroid.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.yzytmac.webrtcandroid.common.*
import com.yzytmac.webrtcandroid.model.Message
import okhttp3.*

class WebSocketService : Service() {
    private val okHttpClient = OkHttpClient.Builder().build()
    private var webSocket: WebSocket? = null

    private var user: String = "123"
    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    companion object {
        var started = false
        fun start(context: Context, user: String) {
            if (!started) {
                val intent = Intent(context, WebSocketService::class.java)
                intent.putExtra("user", user)
                context.startService(intent)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            user = it.getStringExtra("user") ?: "123"
            connect()
        }
        return START_STICKY
    }

    private fun connect() {
        val url = BASE_URL + user
        Log.d("yzy", "CallActivity->connect->第21行:$url")
        okHttpClient.newWebSocket(
            Request.Builder().url(url).build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    IMManager.onWebSocketOpen(user, webSocket)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    try {
                        val message = Gson().fromJson(text, Message::class.java)
                        message?.let {
                            //所有的事件都是对方的动作
                            when (it.event) {
                                EVENT_CALL -> {
                                    Log.e("yzy", "WebSocketService->onMessage->第70行:来电话了")
                                    IMManager.onCall(it)
                                }
                                EVENT_LISTEN -> {
                                    Log.e("yzy", "WebSocketService->onMessage->第74行:对方接听了")
                                    IMManager.onListen(it)
                                }
                                EVENT_REFUSE -> {
                                    Log.e("yzy", "WebSocketService->onMessage->第78行:对方挂断了")
                                    IMManager.onRefuse(it)
                                }
                                EVENT_OFFER -> {
                                    Log.e("yzy", "CallActivity->onMessage->第183行:收到对方offer,直接接听,回复answer")
                                    IMManager.onOffer(it)
                                }
                                EVENT_ANSWER -> {
                                    Log.e("yzy", "CallActivity->onMessage->第190行:收到对方answer,对方接听了")
                                    IMManager.onAnswer(it)
                                }
                                EVENT_CANDIDATE -> {
                                    Log.e("yzy", "CallActivity->onMessage->第191行:收到对方的iceCandidate")
                                    IMManager.onIceCandidate(it)
                                }
                                else -> {
                                    Log.e("yzy", "WebSocketService->onMessage->第85行:收到对方发来的其他消息")
                                    IMManager.onOther(it)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    Log.e("yzy", "CallActivity->onFailure->第44行:" + t.printStackTrace())
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    Log.d("yzy", "CallActivity->onClosed->第49行:$reason")
                }
            })
    }

    override fun onDestroy() {
        started = false
        super.onDestroy()
    }

}
