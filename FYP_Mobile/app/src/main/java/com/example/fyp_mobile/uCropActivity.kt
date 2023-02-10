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
            val destinationUri = intent.getStringExtra("destinationUri")

            val otherTempUri = Uri.fromFile(File(destinationUri!!))

            val destinationUriParse = Uri.parse(Uri.decode(destinationUri))
            val sourceUriParse = Uri.parse(Uri.decode(sourceUri))

            Log.d("URIS", sourceUriParse.toString())
            Log.d("URIS", destinationUriParse.toString())

            val newDestinationUri = File(filesDir, "croppedImage.jpg").toUri()

            UCrop.of(sourceUriParse, newDestinationUri)
                .withAspectRatio(16F, 9F)
                .withMaxResultSize(2000, 2000)
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
            intent.putExtra("CROP", resultUri)
            setResult(102, intent)
            finish()

        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }
}