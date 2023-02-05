package com.example.fyp_mobile

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
class Camera : AppCompatActivity() {

    private lateinit var captureButton: Button
    private lateinit var confirmButton: Button
    private lateinit var resultTextView: TextView
    private val REQUEST_IMAGE_CAPTURE = 101

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        // Requesting camera permissions for camera API
        captureButton = findViewById(R.id.captureButton)
        confirmButton = findViewById(R.id.confirmButton)
        resultTextView = findViewById(R.id.textView)


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
            var pic = data?.getParcelableExtra<Bitmap>("data")
        }
    }


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

}