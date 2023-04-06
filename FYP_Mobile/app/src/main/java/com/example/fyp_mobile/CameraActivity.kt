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
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat


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
    private lateinit var fullSizeImageUri: Uri
    private lateinit var categorySpinner: Spinner
    private lateinit var selectedCategory: String
    private lateinit var receiptId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        // Requesting camera permissions for camera API
        captureButton = findViewById(R.id.captureButton)
        confirmButton = findViewById(R.id.confirmButton)
        imageView = findViewById(R.id.imageView)
        auth = FirebaseAuth.getInstance()
        categorySpinner = findViewById(R.id.categorySpinner)
        val categories = arrayOf("Groceries", "Transport", "Utilities", "Other")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, categories)
        categorySpinner.adapter = adapter

        getPermissions()

        //Capture button listener dispatches camera intent
        captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        confirmButton.setOnClickListener {
            if (uri != null) {
                selectedCategory = categorySpinner.selectedItem.toString()
                receiptId = UUID.randomUUID().toString()
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
        val imageRef = user?.let { storageRef.child("receipts/${it.uid}/${UUID.randomUUID()}.png") }
        confirmButton.isEnabled = false

        val uploadTask = imageRef?.putFile(uri)
        uploadTask?.addOnSuccessListener {
            Log.d(TAG, "Image uploaded successfully")
            Toast.makeText(this, "Receipt Logged", Toast.LENGTH_SHORT).show()

            // Add the image URL to Firestore

            val storagePath = it.metadata?.path

            storagePath?.let { downloadUri ->
                val db = Firebase.firestore
                val imagesToProcess = hashMapOf(
                    "user_id" to user.uid,
                    "image_path" to imageRef.path
                )
                db.collection("images_to_process")
                    .add(imagesToProcess)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        sendImageToOCR(downloadUri.toString(), user.uid)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }?.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to upload image", exception)
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            confirmButton.isEnabled = true
        }
    }

    private fun sendImageToOCR(imagePath: String, userId: String) {
        val client = OkHttpClient()

        val json = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonString = "{\"image_path\": \"$imagePath\", \"user_id\": \"$userId\", \"category\": \"$selectedCategory\", \"id\": \"$receiptId\"}"
        val requestBody = jsonString.toRequestBody(json)

        val request = Request.Builder()
            .url(" https://b4fb-213-133-66-102.eu.ngrok.io/process_image") //Change every time you rehost on ngrok
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Processing started.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Failed to start processing.", Toast.LENGTH_SHORT).show()
                }
            }
        })
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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            fullSizeImageUri = Uri.fromFile(this)
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 101) {
            //Image captured in BitMap format
            tempUri = fullSizeImageUri

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
        if (cameraIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.e("CameraActivity", "Error occurred while creating the image file", ex)
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.example.fyp_mobile.fileprovider",
                    it
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val filesDir = inContext.filesDir
        val imageFile = File(filesDir, "tempImage.png")

        val outputStream = FileOutputStream(imageFile)
        inImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
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