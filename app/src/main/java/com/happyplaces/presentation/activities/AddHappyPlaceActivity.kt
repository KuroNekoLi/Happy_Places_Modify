package com.happyplaces.presentation.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.happyplaces.BuildConfig
import com.happyplaces.R
import com.happyplaces.database.HappyPlace
import com.happyplaces.presentation.HappyPlaceViewModel
import com.happyplaces.presentation.activities.MainActivity.Companion.EXTRA_PLACE_DETAILS
import com.happyplaces.presentation.ui.compose.AddHappyPlaceScreen
import com.happyplaces.presentation.ui.model.AddPlaceEvent
import com.happyplaces.presentation.ui.model.AddPlaceUiState
import lin.example.myapplication.ui.theme.HappyPlacesTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by viewModel<HappyPlaceViewModel>()
    private var calendar = Calendar.getInstance()
    private var photoUri: Uri? = null
    private var happyPlace: HappyPlace? = null

    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onLocationResult(locationResult: LocationResult) {
            val loc: Location = locationResult.lastLocation ?: return
            viewModel.updateCurrentLatLng(loc.latitude, loc.longitude)
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let {
                    viewModel.onImagePicked(it)
                }
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true) {
                photoUri = getPhotoFileUri()
                takePictureLauncher.launch(photoUri!!)
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

    private val pickVisualLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { copyToCache(it).also(viewModel::onImagePicked) }
        }

    private val placeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(res.data!!)
                viewModel.onLocationSelected(
                    addr = place.address ?: "",
                    lat = place.latLng?.latitude ?: 0.0,
                    lng = place.latLng?.longitude ?: 0.0
                )
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

        viewModel.message.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            /** One-off events **/
            uiState.event?.let { e ->
                LaunchedEffect(e) {
                    when (e) {
                        AddPlaceEvent.ShowDatePicker -> showDatePicker()
                        AddPlaceEvent.ShowImagePicker -> {
                            showChooseImageAlertDialog()
                        }

                        AddPlaceEvent.ShowPlacesAutocomplete -> onClickLocation()
                        AddPlaceEvent.RequestCurrentLocation -> requestLocationPermissions()
                        is AddPlaceEvent.ShowToast -> Toast.makeText(
                            this@AddHappyPlaceActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        AddPlaceEvent.NavigateBack -> finish()
                    }
                    viewModel.onEventConsumed()
                }
            }
            HappyPlacesTheme {
                AddHappyPlaceScreen(
                    title = uiState.title,
                    onTitleChange = viewModel::onTitleChange,
                    description = uiState.description,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    date = uiState.date,
                    onDateClick = viewModel::onDateClick,
                    location = uiState.location,
                    onLocationClick = viewModel::onLocationClick,
                    onSelectCurrentLocation = viewModel::onSelectCurrentLocation,
                    imageUri = uiState.imageUri,
                    onAddImageClick = viewModel::onAddImageClick,
                    onSaveClick = viewModel::onSaveClick,
                    onBack = { finish() },
                    toolbarTitle = getString(if (uiState.isEditMode) R.string.edit_happy_place else R.string.add_happy_place),
                    buttonText = if (uiState.isEditMode) getString(R.string.btn_text_update) else getString(
                        R.string.btn_text_save
                    )
                )
            }
        }

        if (happyPlace != null) {
            viewModel.updateUiState(
                AddPlaceUiState(
                    id = happyPlace!!.id,
                    title = happyPlace!!.title.orEmpty(),
                    description = happyPlace!!.description.orEmpty(),
                    date = happyPlace!!.date.orEmpty(),
                    location = happyPlace!!.location.orEmpty(),
                    latitude = happyPlace!!.latitude,
                    longitude = happyPlace!!.longitude,
                    imageUri = happyPlace!!.image,
                    isEditMode = true
                )
            )
        }
    }

    private fun onClickLocation() {
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
        pickVisualLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
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

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, y, m, d ->
                val str = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                    .format(Calendar.getInstance().apply { set(y, m, d) }.time)
                viewModel.onDateSelected(str)
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun requestLocationPermissions() = requestPermissionLauncher.launch(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).toTypedArray()
    )

    /** 將 PhotoPicker 回傳的檔案複製到私有目錄，並取得新 Uri */
    private fun copyToCache(uri: Uri): Uri {
        val dst = File(cacheDir, "picked_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri).use { input ->
            FileOutputStream(dst).use { output -> input?.copyTo(output) }
        }
        return FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", dst)
    }

    private fun showChooseImageAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("選擇選項")
            .setItems(arrayOf("從相簿中選擇", "從相機中選擇")) { _, which ->
                when (which) {
                    0 -> choosePhotoFromGallery()
                    1 -> requestPermissionLauncher.launch(listOf(Manifest.permission.CAMERA).toTypedArray())
                }
            }.show()
    }
}