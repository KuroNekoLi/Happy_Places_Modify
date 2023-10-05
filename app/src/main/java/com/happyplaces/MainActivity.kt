package com.happyplaces

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.happyplaces.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
    }

    //請求權限的Launcher
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            tackPictureLauncher.launch(null)
        }else{
            Toast.makeText(this, "你拒絕了相機權限", Toast.LENGTH_LONG).show()
        }
    }

    //拍照並取得bitmap
    private val tackPictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        binding.imageView.setImageBitmap(it)
    }

//    private val cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//
//            val thumbNail: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                result.data?.extras?.getParcelable("data", Bitmap::class.java)
//            } else {
//                result.data?.extras?.getParcelable("data")
//            }
//
//            binding.imageView.setImageBitmap(thumbNail)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAddHappyPlace.setOnClickListener {
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }
        binding.button.setOnClickListener {
//            checkCameraPermissionAndLaunchCamera()

            // 檢查是否已有相機權限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // 若已有權限，直接啟動相機
                tackPictureLauncher.launch(null)
            } else {
                // 若無權限，請求權限
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

        }
    }

//    private fun checkCameraPermissionAndLaunchCamera() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            cameraResultLauncher.launch(intent)
//        } else {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                checkCameraPermissionAndLaunchCamera()
//            } else {
//                Toast.makeText(this, "你拒絕了相機權限", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
}
