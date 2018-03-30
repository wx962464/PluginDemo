package com.aaron.pluginhookactivity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

/**
 * Created by aaron on 2018/3/30.
 */
class ThirdActivity :Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.textSize = 18.0f
        textView.text = "这个Activity模拟插件中的Activity，并没有在Manifest中进行注册。"
        setContentView(textView)
    }

    override fun onStart() {
        super.onStart()
        Log.v("Aaron","plugin activity onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.v("Aaron","plugin activity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v("Aaron","plugin activity onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v("Aaron","plugin activity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("Aaron","plugin activity onDestroy")
    }
}