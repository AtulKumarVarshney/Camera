package com.androhub.camera

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() {
    lateinit var iv : ImageView
    private var currentImageUri: Uri? = null
    private lateinit var resultLauncherCamera: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iv = findViewById<ImageView>(R.id.iv)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        btnCamera.setOnClickListener {
            openCamera()
        }

        resultLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
                result -> if (result.resultCode== Activity.RESULT_OK)
          {
            handleCaptureImage()
          }
        }

    }
    private fun handleCaptureImage() {
        Glide.with(this).load(currentImageUri).into(iv)
    }

    private fun openCamera() {

        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    val value = ContentValues()
                    value.put(MediaStore.Images.Media.TITLE,"New Picture")
                    value.put(MediaStore.Images.Media.DESCRIPTION,"From your camera")
                    currentImageUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri)
                    resultLauncherCamera.launch(intent)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
                    // Log.d(TAG, "onPermissionsChecked: not granted")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()
    }
}