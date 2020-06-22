package com.yzytmac.webrtcandroid.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yzytmac.webrtcandroid.R
import com.yzytmac.webrtcandroid.service.WebSocketService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_bt.setOnClickListener {
            val user = account_et.text.toString()
            if (user.isEmpty()) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                WebSocketService.start(this, user)
            }
        }
    }

}