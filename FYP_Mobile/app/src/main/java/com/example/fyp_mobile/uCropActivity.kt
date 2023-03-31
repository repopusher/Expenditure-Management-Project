package com.example.fyp_mobile

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.yalantis.ucrop.UCrop
import java.io.File


class uCropActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ucrop)

        val intent = intent
        if (intent.extras != null) {
            val sourceUri = intent.getStringExtra("tempUri")
            val sourceUriParse = Uri.parse(Uri.decode(sourceUri))

            val newDestinationUri = File(filesDir, "croppedImage.jpg").toUri()

            UCrop.of(sourceUriParse, newDestinationUri)
                .withAspectRatio(9F, 16F)
                .withMaxResultSize(2500, 5000)
                .start(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun deleteFileFromPath(path: String) {
        val file = File(path)
        if (file.exists()) {
            if (file.delete()) {
                Log.d("FileUtils", "File deleted successfully")
            } else {
                Log.e("FileUtils", "Failed to delete the file")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)

            Log.d("RESULT", resultUri.toString())

            val intent = Intent()
            intent.putExtra("CROP", resultUri.toString())
            intent.putExtra("originalImagePath", data.getStringExtra("tempUri"))
            setResult(102, intent)
            finish()

            // Delete the temporary file here
            val sourceUri = data.getStringExtra("tempUri")?.let { Uri.parse(Uri.decode(it)) }
            if (sourceUri != null) {
                val sourcePath = sourceUri.path
                if (sourcePath != null) {
                    deleteFileFromPath(sourcePath)
                }
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }
}