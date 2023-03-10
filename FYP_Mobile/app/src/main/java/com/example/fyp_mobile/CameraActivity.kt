package com.example.fyp_mobile

import android.Manifest
import android.annotation.SuppressLint
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
import java.io.ByteArrayOutputStream
import java.io.File
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
            val uid: String = auth.currentUser?.uid.toString()
            val db = Firebase.firestore

            //Helper class for receipt will go here eventually.
            val receipt = hashMapOf(
                "total" to "14.00",
                "date" to "24/02/2023",
                "item" to "Various shopping items here"
            )

            // Add a new document with a generated ID
            db.collection("receipts")
                .document(uid)
                .set(receipt)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
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
        if (requestCode == 101){
            //Image captured in BitMap format
            val photo = data?.getParcelableExtra<Bitmap>("data")

            tempUri = getImageUri(applicationContext, photo!!)

            val finalFile = File(getRealPathFromURI(tempUri))

            Log.println(Log.DEBUG,"debug", finalFile.toString())

            val intent = Intent(this, uCropActivity::class.java)
            intent.putExtra("tempUri", tempUri.toString())
            intent.putExtra("destinationUri", finalFile.toString())
            startActivityForResult(intent, 100)
        }
        if (requestCode == 100 && resultCode == 102) {
            val croppedImageUri = data?.getStringExtra("CROP")
            var uri = data?.data
            uri = Uri.parse(croppedImageUri)

            Log.d("RESULT", "got to req code 102, " + uri.toString())
            imageView.setImageURI(uri)
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

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "TestImage", null)
        return Uri.parse(path)
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