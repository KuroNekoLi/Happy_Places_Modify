package com.happyplaces

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO(Step 6 : Add an activity for Add Happy Place.)
// START
class AddHappyPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        // This is used to align the xml view to this class
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
                .setItems(arrayOf("從相簿中選擇", "從相機中選擇")) { dialog, whitch ->
                    when (whitch) {
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(this, "即將開發相機功能", Toast.LENGTH_LONG).show()
                    }
                }.show()
        }
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    if (report!!.areAllPermissionsGranted()) {
                        Toast.makeText(
                            this@AddHappyPlaceActivity,
                            "確認權限，你現在可在相簿中選擇圖片",
                            Toast.LENGTH_LONG
                        ).show()
                    }


            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check() //因為有withListener
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
}
// END