package com.aaron.pluginhookactivity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        hookActivityHandler()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startHook.setOnClickListener {
            val intent = Intent(this@MainActivity, SecondActivity::class.java)
            val newIntent = Intent()


            // 这里我们把启动的Activity临时替换为插件Activity,并放入真正的intent中
            val componentName = ComponentName(this.packageName, ThirdActivity::class.java.name)
            newIntent.component = componentName

            intent.putExtra(Constants.HOOK_MSG, newIntent)
            startActivity(intent)
        }
    }

    private fun hookActivityHandler()  {
        // 先获取到当前的ActivityThread对象
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        currentActivityThreadField.isAccessible = true;
        val currentActivityThread = currentActivityThreadField.get(null)

        // 由于ActivityThread一个进程只有一个,我们获取这个对象的mH
        val mHField = activityThreadClass.getDeclaredField("mH")
        mHField.isAccessible = true
        val mH = mHField.get(currentActivityThread) as Handler

        val mCallBackField = Handler::class.java.getDeclaredField("mCallback")
        mCallBackField.isAccessible =  true

        mCallBackField.set(mH, ActivityHandlerCallBack(mH))
    }
}
