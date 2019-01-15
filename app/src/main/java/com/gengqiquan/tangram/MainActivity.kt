package com.gengqiquan.tangram

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.gengqiquan.library.SoftInputBugFixedUtil
import com.gengqiquan.library.Tangram

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Tangram.track(this)
        SoftInputBugFixedUtil.assist(this)
    }
}
