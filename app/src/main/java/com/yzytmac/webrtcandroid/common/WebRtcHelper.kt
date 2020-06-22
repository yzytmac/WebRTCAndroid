package com.yzytmac.webrtcandroid.common

import android.content.Context
import android.util.Log
import org.webrtc.*

/**
 * Created by yzy on 2020-06-18 15:37
 * Email: yzytmac@163.com
 * Phone: 18971165201
 * QQ: 398564331
 * Description:
 */
object WebRtcHelper {
    private val streamList = mutableListOf<String>()
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var sdpObserver: SdpObserver? = null
    private val eglBase = EglBase.create()
    private lateinit var context: Context
    private lateinit var localVideoView: SurfaceViewRenderer
    private lateinit var remoteVideoView: SurfaceViewRenderer
    private var onCreateOfferSuccess: ((SessionDescription) -> Unit)? = null
    private var onCreateAnswerSuccess: ((SessionDescription) -> Unit)? = null
    private var onCandidate: ((IceCandidate) -> Unit)? = null

    fun init(
        context: Context,
        localVideoView: SurfaceViewRenderer,
        remoteVideoView: SurfaceViewRenderer,
        onCandidate: (IceCandidate) -> Unit
    ) {
        WebRtcHelper.context = context
        WebRtcHelper.localVideoView = localVideoView
        WebRtcHelper.remoteVideoView = remoteVideoView
        WebRtcHelper.onCandidate = onCandidate
        createPeerConnection()
        initVideoAudio()
        createSdpObserver()
    }

    fun setRemoteDescription(sdp: SessionDescription) {
        Log.e("yzy", "WebRtcUtils->setRemoteDescription->第43行:")
        peerConnection?.setRemoteDescription(sdpObserver, sdp)
    }

    fun addIceCandidate(candidate: IceCandidate) {
        Log.e("yzy", "WebRtcUtils->addIceCandidate->第48行:")
        peerConnection?.addIceCandidate(candidate)
    }


    private fun createPeerConnection() {
        //初始化PeerConnectionFactory
        val options = PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions()
        PeerConnectionFactory.initialize(options)
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, false, true))
            .createPeerConnectionFactory()
        //添加ice服务器
        val iceServer = PeerConnection.IceServer.builder(GOOGLE_ICE_SERVER).createIceServer()
        val iceServers = mutableListOf<PeerConnection.IceServer>(iceServer)
        val rtcConfiguration = PeerConnection.RTCConfiguration(iceServers)
        //创建PeerConnection
        peerConnection = peerConnectionFactory?.createPeerConnection(rtcConfiguration,
            createPeerConnectionObserver()
        )
    }

    private fun createPeerConnectionObserver(): MyPeerConnectionObserver {
        return object : MyPeerConnectionObserver() {
            override fun onIceCandidate(iceCandidate: IceCandidate?) {
                super.onIceCandidate(iceCandidate)
                Log.e("yzy", "CallActivity->onIceCandidate->第232行:")
                iceCandidate?.let {
                    onCandidate?.invoke(it)
                }
            }

            override fun onAddTrack(rtpReceiver: RtpReceiver?, p1: Array<out MediaStream>?) {
                super.onAddTrack(rtpReceiver, p1)
                val track = rtpReceiver?.track()
                if (track is VideoTrack) {
                    Log.e("yzy", "CallActivity->onAddTrack->第244行:视频轨")
                    track.setEnabled(true)
                    track.addSink(remoteVideoView)
                }
                if (track is AudioTrack) {
                    Log.e("yzy", "CallActivity->onAddTrack->第249行:音频轨")
                    track.setVolume(VOLUME)
                }
            }
        }
    }

    fun createOffer(onCreateOfferSuccess: (SessionDescription) -> Unit) {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        WebRtcHelper.onCreateOfferSuccess = onCreateOfferSuccess
        peerConnection?.createOffer(sdpObserver, mediaConstraints)
    }

    fun createAnswer(onCreateAnswerSuccess: (SessionDescription) -> Unit) {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        WebRtcHelper.onCreateAnswerSuccess = onCreateAnswerSuccess
        peerConnection?.createAnswer(sdpObserver, mediaConstraints)
    }

    private fun createSdpObserver() {
        sdpObserver = object : MySdpObserver() {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                super.onCreateSuccess(sdp)
                peerConnection?.setLocalDescription(this, sdp)
                val type = peerConnection?.localDescription?.type
                Log.e("yzy", "CallActivity->onCreateSuccess->第181行:$type")
                when (type) {
                    SessionDescription.Type.OFFER -> {
                        sdp?.let {
                            onCreateOfferSuccess?.invoke(it)
                        }
                    }
                    SessionDescription.Type.ANSWER -> {
                        sdp?.let {
                            onCreateAnswerSuccess?.invoke(it)
                        }
                    }
                    else -> {
                        Log.e("yzy", "CallActivity->onCreateSuccess->第186行:$type")
                    }
                }

            }
        }
    }

    private fun initVideoAudio() {
        initSurfaceView(
            localVideoView,
            true
        )
        initSurfaceView(
            remoteVideoView,
            false
        )
        initVideoCapture()
        initAudioCapture()
    }

    private fun initSurfaceView(surfaceView: SurfaceViewRenderer, z: Boolean = true) {
        surfaceView.init(eglBase.eglBaseContext, null)
        surfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        surfaceView.setMirror(true)
        surfaceView.setEnableHardwareScaler(z)
        surfaceView.setZOrderMediaOverlay(true)
    }

    private fun initAudioCapture() {
        //语音
        val audioConstraints = MediaConstraints()
        //回声消除
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
        //自动增益
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
        //高音过滤
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
        //噪音处理
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
        val audioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
        val audioTrack = peerConnectionFactory?.createAudioTrack(
            AUDIO_TRACK, audioSource)
        val localMediaStream = peerConnectionFactory?.createLocalMediaStream(
            LOCAL_AUDIO_STREAM
        )
        localMediaStream?.addTrack(audioTrack)
        audioTrack?.setVolume(VOLUME)
        peerConnection?.addTrack(audioTrack,
            streamList
        )
        peerConnection?.addStream(localMediaStream)

    }

    private fun initVideoCapture() {
        val videoSource = peerConnectionFactory?.createVideoSource(true)
        val surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().name, eglBase.eglBaseContext)
        val videoCapturer = createVideoCapturer()
        videoCapturer?.initialize(surfaceTextureHelper,
            context, videoSource?.capturerObserver)
        videoCapturer?.startCapture(
            VIDEO_WIDTH,
            VIDEO_HEIGHT,
            VIDEO_FPS
        )
        val videoTrack = peerConnectionFactory?.createVideoTrack(
            VIDEO_TRACK, videoSource)
        videoTrack?.addSink(localVideoView)
        val videoStream = peerConnectionFactory?.createLocalMediaStream(
            LOCAL_VIDEO_STREAM
        )
        videoStream?.addTrack(videoTrack)
        peerConnection?.addTrack(videoTrack,
            streamList
        )
        peerConnection?.addStream(videoStream)
    }

    private fun createVideoCapturer(): VideoCapturer? {
        return if (Camera2Enumerator.isSupported(context)) {
            createCameraCapturer(Camera2Enumerator(context))
        } else {
            createCameraCapturer(Camera1Enumerator(true))
        }
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }
}