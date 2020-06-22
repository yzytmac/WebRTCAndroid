package com.yzytmac.webrtcandroid

import android.util.Log
import org.webrtc.*

/**
 * Created by yzy on 2020-06-17 15:43
 * Email: yzytmac@163.com
 * Phone: 18971165201
 * QQ: 398564331
 * Description:
 */
open class MyPeerConnectionObserver:PeerConnection.Observer {
    override fun onIceCandidate(iceCandidate: IceCandidate?) {
        Log.d("yzy", "PeerConnectionObserver->onIceCandidate->第14行:")
    }

    override fun onDataChannel(p0: DataChannel?) {
        Log.d("yzy", "PeerConnectionObserver->onDataChannel->第19行:")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Log.d("yzy", "PeerConnectionObserver->onIceConnectionReceivingChange->第23行:")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Log.d("yzy", "PeerConnectionObserver->onIceConnectionChange->第27行:")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Log.d("yzy", "PeerConnectionObserver->onIceGatheringChange->第31行:")
    }

    override fun onAddStream(p0: MediaStream?) {
        Log.d("yzy", "PeerConnectionObserver->onAddStream->第35行:")
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Log.d("yzy", "PeerConnectionObserver->onSignalingChange->第39行:")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Log.d("yzy", "PeerConnectionObserver->onIceCandidatesRemoved->第43行:")
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Log.d("yzy", "PeerConnectionObserver->onRemoveStream->第47行:")
    }

    override fun onRenegotiationNeeded() {
        Log.d("yzy", "PeerConnectionObserver->onRenegotiationNeeded->第51行:")
    }

    override fun onAddTrack(rtpReceiver: RtpReceiver?, p1: Array<out MediaStream>?) {
        Log.d("yzy", "PeerConnectionObserver->onAddTrack->第55行:")
    }
}