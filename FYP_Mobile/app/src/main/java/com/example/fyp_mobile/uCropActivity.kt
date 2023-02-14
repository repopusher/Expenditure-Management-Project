package com.example.fyp_mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)

            Log.d("RESULT", resultUri.toString())

            val intent = Intent()
            intent.putExtra("CROP", resultUri.toString())
            setResult(102, intent)
            finish()

        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }
}