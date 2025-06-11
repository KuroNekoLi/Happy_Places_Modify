package com.happyplaces.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.happyplaces.R

/**
 * Activity 啟動器數據類
 */
data class ActivityLaunchers(
    val pickMediaLauncher: androidx.activity.result.ActivityResultLauncher<PickVisualMediaRequest>,
    val cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    val cameraPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    val placeLauncher: androidx.activity.result.ActivityResultLauncher<Intent>,
    val locationPermissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>
)

/**
 * Activity 啟動器輔助類
 */
object ActivityLauncherHelper {

    /**
     * 記住所有活動啟動器
     * @param onImagePicked 圖片選擇回調
     * @param onLocationSelected 位置選擇回調
     * @param onLocationUpdate 位置更新回調
     * @param onCapturedPhotoUri 拍照 URI 回調
     * @param onShowImageDialog 顯示圖片對話框回調
     * @return ActivityLaunchers 實例
     */
    @Composable
    fun rememberActivityLaunchers(
        onImagePicked: (Uri) -> Unit,
        onLocationSelected: (String, Double, Double) -> Unit,
        onLocationUpdate: (Double, Double) -> Unit,
        onCapturedPhotoUri: (Uri?) -> Unit,
        onShowImageDialog: (Boolean) -> Unit
    ): ActivityLaunchers {
        val context = LocalContext.current

        val pickMediaLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onImagePicked(it)
            }
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                // 拍照成功處理邏輯在 cameraPermissionLauncher 中
            }
        }

        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                val capturedUri = ImageUtils.buildFileUri(context)
                onCapturedPhotoUri(capturedUri)
                cameraLauncher.launch(capturedUri)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.camera_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val placeLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(result.data!!)
                    onLocationSelected(
                        place.formattedAddress.orEmpty(),
                        place.location?.latitude ?: 0.0,
                        place.location?.longitude ?: 0.0
                    )
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    result.data?.let { intent ->
                        val status = Autocomplete.getStatusFromIntent(intent)
                        Log.e("PlacePicker", "Places Autocomplete error: ${status.statusMessage}")
                        Toast.makeText(
                            context,
                            "Get place failed, please contact the developer.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                Activity.RESULT_CANCELED -> {
                    Log.i("PlacePicker", "Places Autocomplete canceled by user")
                }
            }
        }

        val locationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _ ->
            handleLocationPermissionResult(context, onLocationUpdate)
        }

        return ActivityLaunchers(
            pickMediaLauncher,
            cameraLauncher,
            cameraPermissionLauncher,
            placeLauncher,
            locationPermissionLauncher
        )
    }

    /**
     * 顯示地點自動完成對話框
     * @param context 上下文
     * @param activityLaunchers 活動啟動器
     */
    fun showPlacesAutocomplete(
        context: Context,
        activityLaunchers: ActivityLaunchers
    ) {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS
        )
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            fields
        ).build(context)
        activityLaunchers.placeLauncher.launch(intent)
    }

    /**
     * 處理位置權限結果
     * @param context 上下文
     * @param onLocationUpdate 位置更新回調
     */
    private fun handleLocationPermissionResult(
        context: Context,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine && hasCoarse) {
            requestCurrentLocation(context, onLocationUpdate)
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.location_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * 請求當前位置
     * @param context 上下文
     * @param onLocationUpdate 位置更新回調
     */
    private fun requestCurrentLocation(
        context: Context,
        onLocationUpdate: (Double, Double) -> Unit
    ) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10_000L
            ).setMinUpdateIntervalMillis(5_000L).build()

            client.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { location ->
                            onLocationUpdate(location.latitude, location.longitude)
                        }
                    }
                },
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
