package com.happyplaces.presentation.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.happyplaces.App
import com.happyplaces.BuildConfig
import com.happyplaces.R
import com.happyplaces.database.HappyPlace
import com.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.happyplaces.presentation.HappyPlaceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//Android Activity Result API (二) ：拍照与选择照片
//https://juejin.cn/post/7082314521284444173

//照片选择器
//https://developer.android.com/training/data-storage/shared/photopicker?hl=zh-cn

//访问共享存储空间中的媒体文件
//https://developer.android.com/training/data-storage/shared/media?hl=zh-cn#kotlin

class AddHappyPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener
    private var photoUri: Uri? = null
    private var latitude = 0.0
    private var longitude = 0.0

    private lateinit var viewModel: HappyPlaceViewModel
    private val factory by lazy { App.instance.factory }


    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let {
                    binding.ivPlaceImage.setImageURI(it)
                }
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                choosePhotoFromGallery()
            } else {
                showRationalDialogForPermissions()
            }
        }

    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                lifecycleScope.launch(IO) {
                    val inputStream = contentResolver.openInputStream(uri)
                    val file = File(getExternalFilesDir(null), "selected_image.jpg")
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    val newPhotoUri = Uri.fromFile(file)
                    withContext(Main) {
                        photoUri = newPhotoUri
                        binding.ivPlaceImage.setImageURI(newPhotoUri)
                    }
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    private val placeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                binding.etLocation.setText(place.address)
                latitude = place.latLng!!.latitude
                longitude = place.latLng!!.longitude
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.google_maps_api_key))
        }

        viewModel = ViewModelProvider(this, factory)[HappyPlaceViewModel::class.java]
        viewModel.message.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddPlace.setNavigationOnClickListener { onBackPressed() }

        dateSetListener = OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()
        }

        binding.etDate.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.tvAddImage.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("選擇選項")
                .setItems(arrayOf("從相簿中選擇", "從相機中選擇")) { _, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePictureFromCamera()
                    }
                }.show()
        }
        binding.btnSave.setOnClickListener {
            Log.i("LinLi", "photoUri: "+photoUri);
            when {
                binding.etTitle.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                }

                binding.etDescription.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT)
                        .show()
                }

                binding.etLocation.text.isNullOrEmpty() -> {
                    Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT)
                        .show()
                }

                photoUri == null -> {
                    Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    CoroutineScope(IO).launch {
                        viewModel.insert(
                            HappyPlace(
                                0,
                                binding.etTitle.text.toString(),
                                photoUri,
                                binding.etDescription.text.toString(),
                                binding.etDate.text.toString(),
                                binding.etLocation.text.toString(),
                                latitude,
                                longitude
                            )
                        )
                    }
                    finish()
                }
            }
        }

        binding.etLocation.setOnClickListener {
            try {
                // These are the list of fields which we required is passed
                val fields = listOf(
                    Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
                )
                // Start the autocomplete intent with a unique request code.
                val intent =
                    Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(applicationContext)
                placeResultLauncher.launch(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private fun getPhotoFileUri(): Uri? {
        val storageFile: File? =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                externalCacheDir
            } else {
                cacheDir
            }

        val photoFile = File.createTempFile("tmp_image_file", ".png", storageFile).apply {
            createNewFile()
            deleteOnExit()
        }
        val fileProviderUri =
            FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", photoFile)
        return fileProviderUri
    }


    private fun choosePhotoFromGallery() {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("您似乎關閉了權限，請開啟您的權限。")
            .setPositiveButton("設定") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("取消") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .show()
    }

    private fun updateDateInView() {
        val myFormat = "yyyy.MM.dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time).toString())
    }

    private fun takePictureFromCamera() {
        val permissions = listOf(Manifest.permission.CAMERA)
        if (arePermissionsGranted(permissions)) {
            photoUri = getPhotoFileUri()
            takePictureLauncher.launch(photoUri)
        } else {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun arePermissionsGranted(permissions: List<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}