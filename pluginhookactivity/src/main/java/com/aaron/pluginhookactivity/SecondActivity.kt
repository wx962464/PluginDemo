package com.aaron.pluginhookactivity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

/**
 * Created by aaron on 2018/3/30.
 * 这是一个假Activity，实际不启动，仅做编译，并注册到manifest，后续通过启动它而达到实际启动插件的Activity
 */
class SecondActivity :Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("Aaron","这是一个SecondActivity")
        val textView = TextView(this)
        textView.text = "这是一个假Activity，实际不启动，仅做编译，并注册到manifest，后续通过启动它而达到实际启动插件的Activity"
        setContentView(textView)
        // 实际上这个Activity是不会显示的。
        finish()
    }
}