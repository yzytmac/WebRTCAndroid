package com.yzytmac.webrtcandroid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.yzytmac.webrtcandroid.*
import com.yzytmac.webrtcandroid.common.IMManager
import kotlinx.android.synthetic.main.activity_call.*

class CallActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, toUser: String, isCall: Boolean) {
            val intent = Intent(context, CallActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("toUser", toUser)
            intent.putExtra("isCall", isCall)//是否主动呼叫
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        val toUser = intent.getStringExtra("toUser")
        val isCall = intent.getBooleanExtra("isCall", true)
        IMManager.init(toUser, local_video_view, remote_video_view)
        if (isCall) {
            IMManager.call(toUser)
            listen_button.visibility = GONE
        } else {
            listen_button.visibility = VISIBLE
        }
        toUser?.let {
            listen_button.setOnClickListener {
                IMManager.listen(toUser)
            }

            refuse_button.setOnClickListener {
                IMManager.refuse(toUser)
            }
        }
    }


}