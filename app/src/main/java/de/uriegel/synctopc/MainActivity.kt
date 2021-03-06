package de.uriegel.synctopc

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import de.uriegel.activityextensions.ActivityRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        launch {
            val backgroundResult =
                activityRequest.checkAndAccessPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            if (backgroundResult.any { !it.value }) {
                toast(R.string.no_access, Toast.LENGTH_LONG)
                finish()
                return@launch
            }

            if (Build.VERSION.SDK_INT >= 30 && !hasAllFilesPermission()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                activityRequest.launch(Intent( Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
            }
        }
    }

    fun onStart(view: View) {
        launch {
            try {
                status.text = ""
                val path = "${Environment.getExternalStorageDirectory()}/Transfer"
                Log.d("Files", "Path: $path")
                val directory = File(path)
                directory.listFiles()
                    .let {
                        Log.d("Files", "Size: " + it.size)
                        it.filter { file -> !file.isDirectory }
                            .sortedBy { file -> file.name }
                            .forEach { file ->
                                uploadFile("http://illmatic:8080/upload?file=${file.name}", file)
                            }
                    }
            } catch(err: Exception) {
                status.text = err.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasAllFilesPermission() = Environment.isExternalStorageManager()

    override val coroutineContext = Dispatchers.Main

    private val activityRequest = ActivityRequest(this)
}