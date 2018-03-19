package com.aaron.plugindemo

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Color
import android.os.Environment
import android.util.Log
import dalvik.system.PathClassLoader
import java.io.File
import android.content.res.AssetManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import dalvik.system.DexClassLoader


class MainActivity : AppCompatActivity() {

    private val requestPermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val permissionCode = 1001
//    private val installedPackageName = "com.cvte.cheetah"
//    private val picPluginInstallName = "icon_app"
    private val installedPackageName = "com.aaron.plugininstall"
    private val picPluginInstallName = "pic_plugin_installed"

    private val picPluginUnInstallName = "pic_plugin_no_install"
    private val uninstallApkPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "pluginUninstall.apk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListener()
    }

    private fun setListener() {
        load_local_image.setOnClickListener {
            Log.v("Aaron","load local image ")
            imageview.setImageResource(R.mipmap.nature)
        }
        load_installed_apk_image.setOnClickListener {
            if(checkPackageInstalled()) {
                // 创建插件的context对象，根据插件的包名
                val pluginContext = this.createPackageContext(installedPackageName, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)

                val resourceId = loadInstallPluginResource(pluginContext)
                Log.v("Aaron","loadPluginResource resourceId = $resourceId ")
                if(resourceId != 0) {
                    textinfo.setTextColor(Color.BLACK)
                    textinfo.setText("加载已安装插件中图片成功")
                    imageview.setImageDrawable(pluginContext.resources.getDrawable(resourceId))

                } else {
                    textinfo.setTextColor(Color.RED)
                    textinfo.setText("加载已安装插件中图片失败")
                }
            } else {
                textinfo.setText("未找到已经安装的插件，请确认已经安装")
                textinfo.setTextColor(Color.RED)
            }
        }
        load_no_install_apk_image.setOnClickListener {
            if(checkPermisson()) {
                if(checkApkFileExist(uninstallApkPath)) {
                    val resource = getUninstallPluginResource(uninstallApkPath)
                    val resId = loadUninstallPluginResourceId(uninstallApkPath)
                    Log.v("Aaron","load no install apk resource id = $resId ")
                    textinfo.setTextColor(Color.BLACK)
                    textinfo.setText("加载未安装插件中图片成功")
                    imageview.setImageDrawable( resource.getDrawable(resId))
                } else {
                    textinfo.setText("未找到未安装的插件文件，请确认已经放入sdcard中")
                    textinfo.setTextColor(Color.RED)
                }
            } else {
                textinfo.setText("没有权限，请开放访问文件的权限")
                textinfo.setTextColor(Color.RED)
            }

        }
    }

    private fun checkPackageInstalled() : Boolean {
        val packPageInfos: List<PackageInfo> = packageManager.getInstalledPackages(0)
        return packPageInfos.any { installedPackageName == it.packageName }
    }

    private fun loadInstallPluginResource(context: Context) : Int {

        // 创建插件的classLoader 加载 资源
        val pathClassLoader = PathClassLoader(context.packageResourcePath, ClassLoader.getSystemClassLoader())

        //通过使用自身的加载器反射出mipmap类进而使用该类的功能
//        val clazz = pathClassLoader.loadClass("$installedPackageName.R\$drawable")
         val clazz = pathClassLoader.loadClass("$installedPackageName.R\$mipmap")
        //参数：1、类的全名，2、是否初始化类，3、加载时使用的类加载器
//        val clazz = Class.forName("$installedPackageName.R\$mipmap", true, pathClassLoader)
        val field = clazz.getDeclaredField(picPluginInstallName)
        return field.getInt(R.mipmap::class.java)
    }

    private fun checkApkFileExist(path: String): Boolean {
        Log.v("Aaron","uninstall apk file path = $path ")
        return File(path).exists()
    }

    private fun  getUninstallApkInfo(path: String): PackageInfo {
        return this.packageManager.getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES)
    }

    private fun getUninstallPluginResource(apkPath: String): Resources {
        val assetManager = AssetManager::class.java.newInstance()
        //反射调用方法addAssetPath(String path)
        val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
        //将未安装的Apk文件的添加进AssetManager中，第二个参数为apk文件的路径带apk名
        addAssetPath.invoke(assetManager, apkPath)
        val superRes = this.resources
        return Resources(assetManager, superRes.displayMetrics,
                superRes.configuration)
    }

    private fun loadUninstallPluginResourceId(apkPath: String): Int {

        val  cache = this.getDir("dex",Context.MODE_PRIVATE)
        val dexClassLoader = DexClassLoader(apkPath, cache.absolutePath, null, ClassLoader.getSystemClassLoader())
        val packageInfo = getUninstallApkInfo(apkPath)

        //通过使用apk自己的类加载器，反射
        val clazz = dexClassLoader.loadClass(packageInfo.packageName + ".R\$mipmap")
        val field = clazz.getDeclaredField(picPluginUnInstallName)//得到名为one的这张图片字段
        return field.getInt(R.mipmap::class.java)//得到图片id

    }

    private fun checkPermisson() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val permission = ActivityCompat.checkSelfPermission(this,requestPermission)
            return if(permission == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(requestPermission),permissionCode)
                false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == permissionCode) {
            for((index,permission) in permissions.withIndex() ) {
                if(permission == requestPermission) {
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED){
                        textinfo.setText("已经获取到文件读取权限")
                        textinfo.setTextColor(Color.BLUE)
                        break
                    }
                }
            }
        }
    }
}
