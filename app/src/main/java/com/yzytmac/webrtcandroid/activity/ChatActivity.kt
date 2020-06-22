package com.yzytmac.webrtcandroid.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.yzytmac.webrtcandroid.App
import com.yzytmac.webrtcandroid.R
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        call_button.setOnClickListener {
            val toUser = toUserEt.text.toString()
            if (toUser.isEmpty()) {
                Toast.makeText(this, "请输入对方账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                CallActivity.start(App.context, toUser, true)//主动呼叫
            }
        }
    }

    companion object{
        fun start(context: Context){
            val intent = Intent(context, ChatActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}