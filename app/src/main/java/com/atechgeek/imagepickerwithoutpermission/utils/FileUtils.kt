package com.atechgeek.imagepickerwithoutpermission.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.atechgeek.imagepickerwithoutpermission.BuildConfig
import com.atechgeek.imagepickerwithoutpermission.R
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    const val IMAGE_REQUEST_CODE = 1
    const val FILE_PICK_TAG = "filePicker"

    fun getPickImageIntent(context: Context?): Intent? {
        var chooserIntent: Intent? = null

        if (context != null && getImageUri(context) != Uri.EMPTY) {
            var intentList: MutableList<Intent> = ArrayList()
            //Intent to show gallery option
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //Intent to add camera option to chooser
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri(context))

            intentList = addIntentsToList(context, intentList, pickIntent)
            intentList = addIntentsToList(context, intentList, takePhotoIntent)

            if (intentList.size > 0) {
                chooserIntent = Intent.createChooser(
                    intentList.removeAt(intentList.size - 1),
                    context.getString(R.string.select_capture_image)
                )

                chooserIntent?.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    intentList.toTypedArray<Parcelable>()
                )
            }
        }

        return chooserIntent
    }


    private fun getImageUri(context: Context?): Uri {
        val imageFile = getImageFile(context)

        if (context != null && imageFile != null) {
            return FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + context.getString(R.string.file_provider_name),
                imageFile
            )
        }

        return Uri.EMPTY
    }


    fun getImageFile(context: Context?): File? {
        if (context != null) {
            val pathname = "${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)}"

            Log.d(FILE_PICK_TAG, "ImageFilePath: $pathname")
            val folder = File(pathname)
            folder.mkdirs()
            val file = File(folder, "image_temp.jpg")
            Log.d(FILE_PICK_TAG, "ImageFile: $file")
            file.createNewFile()
            return file
        }

        return null
    }

    private fun addIntentsToList(
        context: Context,
        list: MutableList<Intent>,
        intent: Intent
    ): MutableList<Intent> {
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.setPackage(packageName)
            list.add(targetedIntent)
        }

        return list
    }

    /*
    *   Get Image file from URI (i.e. Call when you get image from gallery)
    * */

    fun getFileFromUri(context: Context?, uri: Uri): File? {
        val imageFile = getImageFile(context)

        if (context != null && imageFile != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream.use { input ->
                val outputStream = FileOutputStream(imageFile)
                outputStream.use { output ->
                    val buffer = ByteArray(4 * 1024) // buffer size
                    while (true) {
                        val byteCount = input?.read(buffer)

                        if (byteCount != null) {
                            if (byteCount < 0) break
                            output.write(buffer, 0, byteCount)
                        }
                    }
                    output.flush()
                    return imageFile
                }
            }
        }
        return null
    }
}