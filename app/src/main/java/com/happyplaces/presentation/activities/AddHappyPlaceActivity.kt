package com.happyplaces.presentation.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
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
import com.happyplaces.presentation.activities.MainActivity.Companion.EXTRA_PLACE_DETAILS
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
    private lateinit var dateSetListener: OnDateSetListener
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: HappyPlaceViewModel
    private var calendar = Calendar.getInstance()
    private var photoUri: Uri? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var happyPlace: HappyPlace? = null
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
            if (permissions[Manifest.permission.CAMERA] == true) {
                photoUri = getPhotoFileUri()
                takePictureLauncher.launch(photoUri)
            }
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                Toast.makeText(this, "地區權限已開啟", Toast.LENGTH_SHORT).show()
                requestNewLocationData()
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.google_maps_api_key))
        }
        if (intent.hasExtra(EXTRA_PLACE_DETAILS)) {
            happyPlace = intent.getParcelableExtra(EXTRA_PLACE_DETAILS, HappyPlace::class.java)
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
        if (happyPlace != null) {
            supportActionBar?.title = "Edit Happy Place"

            binding.etTitle.setText(happyPlace!!.title)
            binding.etDescription.setText(happyPlace!!.description)
            binding.etDate.setText(happyPlace!!.date)
            binding.etLocation.setText(happyPlace!!.location)
            latitude = happyPlace!!.latitude
            longitude = happyPlace!!.longitude

            photoUri = happyPlace!!.image

            binding.ivPlaceImage.setImageURI(photoUri)

            binding.btnSave.text = "UPDATE"
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
                        1 -> requestPermissionLauncher.launch(listOf(Manifest.permission.CAMERA).toTypedArray())
                    }
                }.show()
        }
        binding.btnSave.setOnClickListener {
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

                happyPlace == null -> {
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

                else -> {
                    lifecycleScope.launch(IO) {
                        happyPlace = happyPlace!!.copy(
                            title = binding.etTitle.text.toString(),
                            image = photoUri,
                            description = binding.etDescription.text.toString(),
                            date = binding.etDate.text.toString(),
                            location = binding.etLocation.text.toString(),
                            latitude = latitude,
                            longitude = longitude
                        )

                        viewModel.update(happyPlace!!)
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

        binding.tvSelectCurrentLocation.setOnClickListener {
            requestPermissionLauncher.launch(
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).toTypedArray()
            )
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

    //更改位置信息设置
    //https://developer.android.com/training/location/change-location-settings?hl=zh-cn
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create()
            .setInterval(10000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            latitude = mLastLocation!!.latitude
            Log.e("Current Latitude", "$latitude")
            longitude = mLastLocation.longitude
            Log.e("Current Longitude", "$longitude")

            viewModel.getAddressFromLatLng(latitude, longitude)
            viewModel.address.observe(this@AddHappyPlaceActivity) {
                binding.etLocation.setText(it)
            }
        }
    }
}