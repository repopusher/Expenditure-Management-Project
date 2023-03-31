package com.example.fyp_mobile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


@Suppress("DEPRECATION")
class CameraActivity : AppCompatActivity() {

    private lateinit var captureButton: Button
    private lateinit var confirmButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var imageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 101
    private var tempUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var uri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        // Requesting camera permissions for camera API
        captureButton = findViewById(R.id.captureButton)
        confirmButton = findViewById(R.id.confirmButton)
        resultTextView = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)
        auth = FirebaseAuth.getInstance()

        getPermissions()

        //Capture button listener dispatches camera intent
        captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        confirmButton.setOnClickListener {
            if (uri != null) {
                uploadImage(uri!!)
            } else {
                Toast.makeText(this, "No image to upload", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun uploadImage(uri: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val user = auth.currentUser
        val imageRef = user?.let { storageRef.child("receipts/${it.uid}/${UUID.randomUUID()}.jpg") }
        confirmButton.isEnabled = false

        val uploadTask = imageRef?.putFile(uri)
        uploadTask?.addOnSuccessListener {
            Log.d(TAG, "Image uploaded successfully")
            Toast.makeText(this, "Receipt Logged", Toast.LENGTH_SHORT).show()
        }?.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to upload image", exception)
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            confirmButton.isEnabled = true
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
        super.onBackPressed()
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 101) {
            //Image captured in BitMap format
            val photo = data?.getParcelableExtra<Bitmap>("data")

            tempUri = getImageUri(applicationContext, photo!!)

            val finalFile = File(getRealPathFromURI(tempUri))

            Log.println(Log.DEBUG, "debug", finalFile.toString())

            val intent = Intent(this, uCropActivity::class.java)
            intent.putExtra("tempUri", tempUri.toString())
            intent.putExtra("destinationUri", finalFile.toString())
            startActivityForResult(intent, 100)
        }
        else if (requestCode == 100 && resultCode == 102) {
            val croppedImageUri = data?.getStringExtra("CROP")
            val originalImagePath = data?.getStringExtra("originalImagePath")

            uri = Uri.parse(croppedImageUri)

            Log.d("RESULT", "got to req code 102, " + uri.toString())
            imageView.setImageURI(null) // Reset the image view
            imageView.setImageURI(uri) // Set the new image
            confirmButton.isEnabled = true // Enable the confirm buttom

            // Delete the original image file here
            if (originalImagePath != null) {
                deleteFileFromPath(originalImagePath)
            }
        }
        else if (requestCode == 100 && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "No new image captured", Toast.LENGTH_SHORT).show()
        }
        else {
            if (imageView.drawable == null) {
                // Set the default image
                imageView.setImageResource(R.drawable.reciept)
            } else {
                // Keep the previous image in the imageView
                Toast.makeText(this, "No new image captured", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun getPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val permission = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permission, 1122)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 1123)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permission, 1124)
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

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val filesDir = inContext.filesDir
        val imageFile = File(filesDir, "tempImage.jpg")

        val outputStream = FileOutputStream(imageFile)
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        return Uri.fromFile(imageFile)
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (contentResolver != null) {
            val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

}