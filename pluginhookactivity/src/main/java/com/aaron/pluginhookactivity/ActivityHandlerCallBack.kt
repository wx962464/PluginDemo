package com.aaron.pluginhookactivity

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log


/**
 * Created by aaron on 2018/3/29.
 */
internal class ActivityHandlerCallBack(private var mBase: Handler) : Handler.Callback {

    override fun handleMessage(msg: Message): Boolean {

        if (msg.what == Constants.LAUNCH_ACTIVITY) {
            handleLaunchActivity(msg)
        }
        mBase.handleMessage(msg)
        return true
    }

    private fun handleLaunchActivity(msg: Message) {
        // 这里简单起见,直接取出TargetActivity;

        val obj = msg.obj

        try {
            // 取出启动的intent
            val intent = obj.javaClass.getDeclaredField("intent")
            intent.isAccessible = true
            val raw = intent.get(obj) as Intent
            //获取插件的Intent，然后将其设置到真正要启动的Activity
            val target = raw.getParcelableExtra<Intent>(Constants.HOOK_MSG)
            Log.v("Aaron", "target = " + target.toString())
            raw.component = target.component

        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }
}
