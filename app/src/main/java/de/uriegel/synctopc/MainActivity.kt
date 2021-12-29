package de.uriegel.synctopc

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import de.uriegel.activityextensions.ActivityRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                startActivity(Intent( Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
            }

            //val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera"
            val path = Environment.getExternalStorageDirectory().toString() + "/Pictures"
            Log.d("Files", "Path: $path")
            val directory = File(path)
            val files = directory.listFiles()
            Log.d("Files", "Size: " + files?.size)
            for (i in files!!.indices) {
                Log.d("Files", "FileName:" + files[i].name)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasAllFilesPermission() = Environment.isExternalStorageManager()

    override val coroutineContext = Dispatchers.Main

    private val activityRequest = ActivityRequest(this)
}