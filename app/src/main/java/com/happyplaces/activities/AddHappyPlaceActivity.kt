package com.happyplaces.activities

import android.Manifest
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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.happyplaces.BuildConfig
import com.happyplaces.databinding.ActivityAddHappyPlaceBinding
import java.io.File
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

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let {
                binding.ivPlaceImage.setImageURI(it)
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.all { it.value }) {
            choosePhotoFromGallery()
        } else {
            showRationalDialogForPermissions()
        }
    }

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            binding.ivPlaceImage.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoUri = getPhotoFileUri()
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
        val fileProviderUri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", photoFile)
        return fileProviderUri
    }


    private fun choosePhotoFromGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("您似乎關閉了權限，請開啟您的權限。")
            .setPositiveButton("設定"){ _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("取消"){ dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
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
            takePictureLauncher.launch(photoUri)
        } else {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun arePermissionsGranted(permissions: List<String>): Boolean {
        return permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }
}