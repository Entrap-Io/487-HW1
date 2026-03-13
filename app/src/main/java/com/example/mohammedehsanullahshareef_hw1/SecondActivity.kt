package com.example.mohammedehsanullahshareef_hw1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SecondActivity : AppCompatActivity() {

    private lateinit var imagePickerZone: FrameLayout
    private lateinit var previewImage: ImageView
    private lateinit var uploadPlaceholder: LinearLayout
    private lateinit var cameraBtn: Button
    private lateinit var galleryBtn: Button
    private lateinit var ratingLabel: TextView
    private lateinit var ratingSeekBar: SeekBar
    private lateinit var categoryStatsLabel: TextView
    private lateinit var browseClosetBtn: Button
    private lateinit var addToClosetBtn: Button
    private lateinit var uploadProgress: LinearLayout
    private lateinit var rootView: View

    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null
    private var currentRating = 5

    private val db by lazy { AppDatabase.getInstance(this).clothingDao() }

    // Camera
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            selectedImageUri = cameraImageUri
            showPreview(selectedImageUri!!)
        }
    }

    // Gallery
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            showPreview(it)
        }
    }

    // Browse Closet — expects CategoryStats back
    private val browseClosetLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val stats = result.data?.getParcelableExtra<CategoryStats>(Constants.EXTRA_CATEGORY_STATS)
            stats?.let {
                categoryStatsLabel.visibility = View.VISIBLE
                categoryStatsLabel.text = buildString {
                    if (it.topCount > 0)    appendLine("👕 Tops avg: ${"%.1f".format(it.topAvg)}/10")
                    if (it.bottomCount > 0) appendLine("👖 Bottoms avg: ${"%.1f".format(it.bottomAvg)}/10")
                    if (it.shoesCount > 0)  appendLine("👟 Shoes avg: ${"%.1f".format(it.shoesAvg)}/10")
                }.trim()
                Snackbar.make(rootView, "Closet stats loaded!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    // Camera permission
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera()
        else Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rootView           = findViewById(R.id.main)
        imagePickerZone    = findViewById(R.id.imagePickerZone)
        previewImage       = findViewById(R.id.previewImage)
        uploadPlaceholder  = findViewById(R.id.uploadPlaceholder)
        cameraBtn          = findViewById(R.id.cameraBtn)
        galleryBtn         = findViewById(R.id.galleryBtn)
        ratingLabel        = findViewById(R.id.ratingLabel)
        ratingSeekBar      = findViewById(R.id.ratingSeekBar)
        categoryStatsLabel = findViewById(R.id.categoryStatsLabel)
        browseClosetBtn    = findViewById(R.id.browseClosetBtn)
        addToClosetBtn     = findViewById(R.id.addToClosetBtn)
        uploadProgress     = findViewById(R.id.uploadProgress)

        setupSeekBar()
        setupButtons()
    }

    private fun setupSeekBar() {
        ratingLabel.text = "Rating: 5 / 10"
        ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                currentRating = progress + 1
                ratingLabel.text = "Rating: $currentRating / 10"
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
    }

    private fun setupButtons() {
        imagePickerZone.setOnClickListener { pickImageLauncher.launch("image/*") }

        cameraBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        galleryBtn.setOnClickListener { pickImageLauncher.launch("image/*") }

        browseClosetBtn.setOnClickListener {
            browseClosetLauncher.launch(Intent(this, ThirdActivity::class.java))
        }

        addToClosetBtn.setOnClickListener {
            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uploadItem()
        }
    }

    private fun launchCamera() {
        val photoFile = createImageFile()
        cameraImageUri = FileProvider.getUriForFile(
            this, "${packageName}.fileprovider", photoFile
        )
        takePictureLauncher.launch(cameraImageUri!!)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timestamp}_", ".jpg", storageDir)
    }

    private fun showPreview(uri: Uri) {
        previewImage.visibility = View.VISIBLE
        uploadPlaceholder.visibility = View.GONE
        Glide.with(this).load(uri).centerCrop().into(previewImage)
    }

    private fun uploadItem() {
        val uri = selectedImageUri ?: return

        setUploading(true)

        lifecycleScope.launch {
            try {
                // Resolve URI to a temp file so Retrofit can read it
                val file = uriToFile(uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.uploadItem(part)
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val apiItem = response.body()!!.item!!

                    // Save to Room
                    val entity = ClothingItemEntity(
                        id           = apiItem.id,
                        imagePath    = file.absolutePath,
                        imageUrl     = apiItem.imageUrl,
                        category     = apiItem.category,
                        subcategory  = apiItem.subcategory ?: "",
                        primaryColor = apiItem.primaryColor,
                        secondaryColor = apiItem.secondaryColor,
                        style        = apiItem.style ?: "",
                        pattern      = apiItem.pattern ?: "",
                        material     = apiItem.material,
                        fit          = apiItem.fit,
                        season       = apiItem.season?.joinToString(",") ?: "",
                        occasionTags = apiItem.occasionTags?.joinToString(",") ?: "",
                        description  = apiItem.description,
                        rating       = currentRating,
                        dateAdded    = apiItem.dateAdded ?: ""
                    )
                    db.insert(entity)

                    setUploading(false)
                    Snackbar.make(
                        rootView,
                        "✅ ${apiItem.subcategory ?: apiItem.category} added to closet!",
                        Snackbar.LENGTH_LONG
                    ).show()

                    // Reset form
                    selectedImageUri = null
                    previewImage.visibility = View.GONE
                    uploadPlaceholder.visibility = View.VISIBLE
                    ratingSeekBar.progress = 4
                    ratingLabel.text = "Rating: 5 / 10"

                } else {
                    setUploading(false)
                    val errMsg = response.errorBody()?.string() ?: "Upload failed"
                    showAlertDialog("Upload Error", errMsg)
                }

            } catch (e: Exception) {
                setUploading(false)
                Toast.makeText(this@SecondActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUploading(uploading: Boolean) {
        uploadProgress.visibility = if (uploading) View.VISIBLE else View.GONE
        addToClosetBtn.isEnabled  = !uploading
        cameraBtn.isEnabled       = !uploading
        galleryBtn.isEnabled      = !uploading
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)!!
        val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
        tempFile.outputStream().use { inputStream.copyTo(it) }
        return tempFile
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
