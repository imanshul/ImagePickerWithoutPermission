package com.atechgeek.imagepickerwithoutpermission

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }

    fun isPermissionGranted(permission: String): Boolean {
        val context = this

        if (context != null) {
            return ContextCompat
                .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        return false
    }


    fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        var isGranted = true

        for (grantResult in grantResults) {
            isGranted = grantResult == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                break
            }
        }

        return isGranted
    }

    fun redirectUserToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
}