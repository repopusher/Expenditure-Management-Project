package com.example.fyp_mobile

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.util.*


@Suppress("DEPRECATION")
class Camera : AppCompatActivity() {

    private lateinit var captureButton: Button
    private lateinit var confirmButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var imageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 101
    private var tempUri: Uri? = null
    private lateinit var destinationUri: Uri


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        // Requesting camera permissions for camera API
        captureButton = findViewById(R.id.captureButton)
        confirmButton = findViewById(R.id.confirmButton)
        resultTextView = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)


        getPermissions()

        //Capture button listener dispatches camera intent
        captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101){
            //Image captured in BitMap format
            val photo = data?.getParcelableExtra<Bitmap>("data")

            imageView.setImageBitmap(photo)

            tempUri = getImageUri(applicationContext, photo!!)

            Toast.makeText(this, tempUri.toString(), Toast.LENGTH_SHORT).show()

            val otherTempUri = java.lang.StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
            destinationUri = Uri.parse(otherTempUri)

            UCrop.of(tempUri!!, destinationUri)
                .withAspectRatio(16F, 9F)
                .withMaxResultSize(2000, 2000)
                .start(this)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//            val resultUri = UCrop.getOutput(data!!)
//        } else if (resultCode == UCrop.RESULT_ERROR) {
//            val cropError = UCrop.getError(data!!)
//        }
//    }


    private fun getPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val permission = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permission, 1122)
        }
    }

    private fun dispatchTakePictureIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "An error occurred, please try again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }

}