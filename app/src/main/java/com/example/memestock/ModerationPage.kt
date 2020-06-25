package com.example.memestock

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ModerationPage : AppCompatActivity() {
    var username = ""
    var token=""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        val intent = getIntent()
        username = intent.extras.get("Username").toString()
        token=intent.extras.get("Token").toString()
    }






}
