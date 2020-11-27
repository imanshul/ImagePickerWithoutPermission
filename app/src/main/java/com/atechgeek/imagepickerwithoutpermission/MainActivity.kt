package com.atechgeek.imagepickerwithoutpermission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.atechgeek.imagepickerwithoutpermission.extension.loadCircularImageWithoutCache
import com.atechgeek.imagepickerwithoutpermission.utils.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setClicks()
    }

    private fun setClicks() {
        ivAdd.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        when (p0) {
            ivAdd -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isPermissionGranted(Manifest.permission.CAMERA))
                        pickImage()
                    else
                        requestPermissions(
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                } else {
                    pickImage()
                }
            }
        }
    }

    private fun pickImage() {
        startActivityForResult(FileUtils.getPickImageIntent(this), FileUtils.IMAGE_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val isGranted = isAllPermissionsGranted(grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (isGranted) {
                    pickImage()
                } else {
                    val showRationale = shouldShowRequestPermissionRationale(permissions[0])
                    if (!showRationale) {
                        /*
                        *   Permissions are denied permanently, redirect to permissions page
                        * */
                        Toast.makeText(
                            this,
                            getString(R.string.error_camera_permission),
                            Toast.LENGTH_LONG
                        ).show()
                        redirectUserToAppSettings()
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.error_camera_permission),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FileUtils.IMAGE_REQUEST_CODE -> {
                    var imageFile: File? = null
                    if (data?.data != null) {
                        //Photo from gallery.
                        imageFile = FileUtils.getFileFromUri(this, data.data!!)
                    } else {
                        //Photo from camera.
                        imageFile = FileUtils.getImageFile(this)
                    }

                    if (imageFile != null) {
                        ivProfilePic.loadCircularImageWithoutCache(imageFile.absolutePath)
                        Log.d(FileUtils.FILE_PICK_TAG, "File Path : ${imageFile.absolutePath}")
                    } else {
                        Log.e(FileUtils.FILE_PICK_TAG, "Error getting file")
                    }

                }
            }
        }
    }
}