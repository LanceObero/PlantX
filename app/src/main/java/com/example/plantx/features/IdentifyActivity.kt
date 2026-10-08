package com.example.plantx.features

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.plantx.R
import com.example.plantx.api.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class IdentifyActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var cardResult: MaterialCardView

    private lateinit var tvPlantName: TextView
    private lateinit var tvScientificName: TextView
    private lateinit var tvConfidence: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvKeyFeatures: TextView
    private lateinit var tvResultMessage: TextView

    private lateinit var progressConfidence: ProgressBar
    private lateinit var progressLoading: ProgressBar

    private lateinit var btnCamera: MaterialButton
    private lateinit var btnSelectImage: MaterialButton
    private lateinit var btnIdentifyAnother: MaterialButton

    companion object {
        private const val CAMERA_REQUEST = 100
        private const val GALLERY_REQUEST = 101
        private const val CAMERA_PERMISSION_REQUEST = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_identify)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        imagePreview = findViewById(R.id.imagePreview)
        cardResult = findViewById(R.id.cardResult)

        tvPlantName = findViewById(R.id.tvPlantName)
        tvScientificName = findViewById(R.id.tvScientificName)
        tvConfidence = findViewById(R.id.tvConfidence)
        tvDescription = findViewById(R.id.tvDescription)
        tvKeyFeatures = findViewById(R.id.tvKeyFeatures)
        tvResultMessage = findViewById(R.id.tvResultMessage)

        progressConfidence = findViewById(R.id.progressConfidence)
        progressLoading = findViewById(R.id.progressLoading)

        btnCamera = findViewById(R.id.btnCamera)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnIdentifyAnother = findViewById(R.id.btnIdentifyAnother)

        btnCamera.setOnClickListener {
            openCamera()
        }

        btnSelectImage.setOnClickListener {
            openGallery()
        }

        btnIdentifyAnother.setOnClickListener {
            resetResult()
        }
    }

    private fun openCamera() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )

        } else {

            launchCamera()
        }
    }

    private fun launchCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (intent.resolveActivity(packageManager) != null) {

            startActivityForResult(
                intent,
                CAMERA_REQUEST
            )

        } else {

            Toast.makeText(
                this,
                "No camera app is available.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openGallery() {

        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"

        startActivityForResult(
            intent,
            GALLERY_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                launchCamera()

            } else {

                Toast.makeText(
                    this,
                    "Camera permission is required to take a photo.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {

            CAMERA_REQUEST -> {

                val bitmap =
                    data?.extras?.get("data") as? Bitmap

                if (bitmap != null) {

                    imagePreview.setImageBitmap(bitmap)
                    imagePreview.visibility = View.VISIBLE

                    val file = bitmapToFile(bitmap)

                    uploadImage(file)
                }
            }

            GALLERY_REQUEST -> {

                val uri: Uri? = data?.data

                if (uri != null) {

                    imagePreview.setImageURI(uri)
                    imagePreview.visibility = View.VISIBLE

                    val file = uriToFile(uri)

                    if (file != null) {

                        uploadImage(file)

                    } else {

                        Toast.makeText(
                            this,
                            "Could not process image.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun uploadImage(file: File) {

        cardResult.visibility = View.GONE
        progressLoading.visibility = View.VISIBLE

        btnCamera.isEnabled = false
        btnSelectImage.isEnabled = false
        btnIdentifyAnother.isEnabled = false

        lifecycleScope.launch {

            try {

                val requestFile =
                    file.asRequestBody(
                        "image/*".toMediaTypeOrNull()
                    )

                val imagePart =
                    MultipartBody.Part.createFormData(
                        "image",
                        file.name,
                        requestFile
                    )

                val preferences =
                    getSharedPreferences(
                        "PlantXPrefs",
                        MODE_PRIVATE
                    )

                val accessToken =
                    preferences.getString(
                        "access_token",
                        null
                    )

                if (accessToken.isNullOrEmpty()) {

                    progressLoading.visibility = View.GONE

                    btnCamera.isEnabled = true
                    btnSelectImage.isEnabled = true
                    btnIdentifyAnother.isEnabled = true

                    Toast.makeText(
                        this@IdentifyActivity,
                        "Please login again.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }

                val response =
                    RetrofitClient.plantApi.identifyPlant(
                        imagePart
                    )

                progressLoading.visibility = View.GONE

                btnCamera.isEnabled = true
                btnSelectImage.isEnabled = true
                btnIdentifyAnother.isEnabled = true

                if (response.isSuccessful) {

                    val result = response.body()

                    if (result != null && result.success) {

                        val plant = result.plant

                        if (plant != null) {

                            showResult(
                                plant.common_name ?: "Unknown plant",
                                plant.scientific_name ?: "Scientific name unavailable",
                                plant.confidence ?: "Low",
                                plant.description ?: "No description available.",
                                plant.key_features ?: "No key features available.",
                                result.message ?: "Plant identified successfully."
                            )

                        } else {

                            Toast.makeText(
                                this@IdentifyActivity,
                                "No plant information received.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else {

                        Toast.makeText(
                            this@IdentifyActivity,
                            result?.message ?: "Plant identification failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    val errorBody =
                        response.errorBody()?.string()

                    Toast.makeText(
                        this@IdentifyActivity,
                        "Upload failed: ${response.code()}\n$errorBody",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                progressLoading.visibility = View.GONE

                btnCamera.isEnabled = true
                btnSelectImage.isEnabled = true
                btnIdentifyAnother.isEnabled = true

                Toast.makeText(
                    this@IdentifyActivity,
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showResult(
        commonName: String,
        scientificName: String,
        confidence: String,
        description: String,
        keyFeatures: String,
        message: String
    ) {

        tvPlantName.text = formatPlantName(commonName)

        tvScientificName.text = scientificName

        tvConfidence.text =
            "Confidence: $confidence"

        val confidenceValue =
            when (confidence.lowercase()) {
                "high" -> 90
                "medium" -> 65
                "low" -> 35
                else -> 50
            }

        progressConfidence.progress = confidenceValue

        tvDescription.text = description

        tvKeyFeatures.text = keyFeatures

        tvResultMessage.text = message

        cardResult.visibility = View.VISIBLE
    }

    private fun formatPlantName(name: String): String {

        return name
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") {
                it.replaceFirstChar { character ->
                    character.uppercase()
                }
            }
    }

    private fun resetResult() {

        cardResult.visibility = View.GONE

        imagePreview.visibility = View.GONE
        imagePreview.setImageDrawable(null)

        progressLoading.visibility = View.GONE

        btnCamera.isEnabled = true
        btnSelectImage.isEnabled = true
        btnIdentifyAnother.isEnabled = true
    }

    private fun bitmapToFile(bitmap: Bitmap): File {

        val file = File(
            cacheDir,
            "plant_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(file).use { outputStream ->

            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                90,
                outputStream
            )
        }

        return file
    }

    private fun uriToFile(uri: Uri): File? {

        val file = File(
            cacheDir,
            "plant_${System.currentTimeMillis()}.jpg"
        )

        return try {

            val inputStream =
                contentResolver.openInputStream(uri)

            if (inputStream == null) {
                return null
            }

            inputStream.use { input ->

                FileOutputStream(file).use { output ->

                    input.copyTo(output)
                }
            }

            file

        } catch (e: Exception) {

            null
        }
    }
}

