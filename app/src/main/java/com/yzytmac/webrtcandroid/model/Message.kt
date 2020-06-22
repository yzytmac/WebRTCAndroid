package com.yzytmac.webrtcandroid.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

data class Message(
    val from_user: String,
    val to_user: String,
    val event: String,
    val sdp: SessionDescription? = null,
    val iceCandidate: IceCandidate? = null
) {
    fun toJsonString(): String {
        return Gson().toJson(this)
    }
}