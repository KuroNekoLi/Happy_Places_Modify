package com.happyplaces.presentation.ui.compose

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.happyplaces.BuildConfig
import com.happyplaces.R
import com.happyplaces.data.model.AddPlaceEvent
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import com.happyplaces.util.SetupPreviewKoin
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddHappyPlaceScreen(
    id: String? = null,
    viewModel: HappyPlaceViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    id?.let {
        LaunchedEffect(Unit) {
            viewModel.getHappyPlaceById(id)
        }
    }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    // --- Photo Picker & Camera ---
    var capturedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showImageDialog by remember { mutableStateOf(false) }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, flag)
            viewModel.onImagePicked(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) capturedPhotoUri?.let(viewModel::onImagePicked) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            capturedPhotoUri = buildFileUri(context)
            capturedPhotoUri?.let {
                cameraLauncher.launch(it)
            }
        } else {
            Toast.makeText(context, "相機權限被拒絕", Toast.LENGTH_SHORT).show()
        }
    }
    // --- Places Autocomplete ---
    val placeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            viewModel.onLocationSelected(
                addr = place.formattedAddress.orEmpty(),
                lat = place.location?.latitude ?: 0.0,
                lng = place.location?.longitude ?: 0.0
            )
        } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
            result.data?.let { intent ->
                val status = Autocomplete.getStatusFromIntent(intent)
                Log.e("LinLi", "Places Autocomplete error: ${status.statusMessage}")
                Toast.makeText(
                    context,
                    "Get place failed,please contract with the developer.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Log.i("LinLi", "Places Autocomplete canceled by user")
        }
    }
    // --- Location Permissions & Updates ---
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        // 再次用 checkSelfPermission 顯式檢查
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine && hasCoarse) {
            // 取得 FusedLocationProviderClient
            val client = LocationServices.getFusedLocationProviderClient(context)
            // 安全呼叫 requestLocationUpdates()
            try {
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,    // 高精度模式
                    10_000L                              // 更新間隔：10 秒
                )
                    .setMinUpdateIntervalMillis(5_000L) // 最快更新間隔：5 秒 :contentReference[oaicite:0]{index=0}
                    .build()
                // 2. 呼叫 requestLocationUpdates 時傳入 builder.build() 的結果
                client.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            result.lastLocation?.let { loc ->
                                viewModel.updateCurrentLatLng(loc.latitude, loc.longitude)
                            }
                        }
                    },
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                // 最後保險：避免異常閃退
                e.printStackTrace()
            }
        } else {
            // 權限被拒，提示或引導設定
            Toast.makeText(context, "位置權限被拒絕", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.google_maps_api_key))
        }
    }
    /** One-off events **/
    uiState.event?.let { e ->
        LaunchedEffect(e) {
            when (e) {
                AddPlaceEvent.ShowDatePicker -> showDatePicker(context) {
                    viewModel.onDateSelected(it)
                }

                AddPlaceEvent.ShowImagePicker -> showImageDialog = true

                AddPlaceEvent.ShowPlacesAutocomplete -> {
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
                    placeLauncher.launch(intent)
                }

                AddPlaceEvent.RequestCurrentLocation -> {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                is AddPlaceEvent.ShowToast -> Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()

                AddPlaceEvent.NavigateBack -> onBack()
            }
            viewModel.onEventConsumed()
        }
    }

    // 顯示來源選擇對話框
    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = { Text("選擇圖片來源") },
            text = {
                Column {
                    TextButton(onClick = {
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                        showImageDialog = false
                    }) { Text("從相簿中選擇") }
                    TextButton(onClick = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        showImageDialog = false
                    }) { Text("從相機中拍照") }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

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
        imageUrl = uiState.imageUrl,
        onAddImageClick = viewModel::onAddImageClick,
        onSaveClick = {
            viewModel.onSaveClick()
        },
        onBack = onBack,
        toolbarTitle = stringResource(if (uiState.isEditMode) R.string.edit_happy_place else R.string.add_happy_place),
        buttonText = stringResource(if (uiState.isEditMode) R.string.btn_text_update else R.string.btn_text_save)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHappyPlaceScreen(
    toolbarTitle: String,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    date: String,
    buttonText: String,
    onDateClick: () -> Unit,
    location: String,
    onLocationClick: () -> Unit,
    onSelectCurrentLocation: () -> Unit,
    imageUrl: String?,
    onAddImageClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit,
) {
    val textFieldColor = TextFieldDefaults.colors().copy(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,          // 文字
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,   // 標籤
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,   // Placeholder
        disabledIndicatorColor = MaterialTheme.colorScheme.outline,            // 外框線
        disabledContainerColor = Color.Transparent                             // 背景
    )
    Scaffold(
        topBar = {
            HappyPlaceToolBar(true, toolbarTitle, onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),         // main_content_padding
            verticalArrangement = Arrangement.spacedBy(16.dp)  // add_screen_til_marginTop
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.edit_text_hint_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.edit_text_hint_description)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Date (readOnly + 點擊彈出 DatePicker)
            OutlinedTextField(
                value = if (date.isBlank()) "Date" else date,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDateClick()
                    },
                colors = textFieldColor
            )

            // Location (readOnly + 點擊彈出地圖/選擇)
            OutlinedTextField(
                value = location,
                onValueChange = { /* no-op */ },
                enabled = false,
                label = { Text(stringResource(R.string.edit_text_hint_location)) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLocationClick() },
                colors = textFieldColor
            )

            // 選取目前位置
            Text(
                text = stringResource(R.string.add_place_select_current_location_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(8.dp),            // shape_image_view_border
                        color = Color.Transparent
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectCurrentLocation() }
                    .padding(12.dp)                                  // add_place_select_current_location_padding
            )

            // 圖片 & 加圖按鈕
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)                               // add_screen_place_image_size
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onAddImageClick() }
                        .padding(8.dp),                              // add_screen_place_image_padding
                ) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(id = R.drawable.add_screen_image_placeholder),
                    )
                }

                Text(
                    text = stringResource(R.string.text_add_image),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onAddImageClick() }
                        .padding(8.dp)                              // add_screen_text_add_image_padding
                )
            }

            // Save 按鈕
            Button(
                onClick = onSaveClick,
                shape = RoundedCornerShape(16.dp),                 // shape_button_rounded
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// Helper: 建立暫存檔並回傳 Uri
private fun buildFileUri(context: Context): Uri {
    val storage = if (
        android.os.Environment.MEDIA_MOUNTED == android.os.Environment.getExternalStorageState()
    ) context.externalCacheDir else context.cacheDir
    val file = File.createTempFile("tmp_img", ".jpg", storage).apply { deleteOnExit() }
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.provider",
        file
    )
}

// Helper: 顯示 DatePickerDialog
private fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val cal = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, y, m, d ->
            val fmt = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val dateStr = fmt.format(Calendar.getInstance().apply { set(y, m, d) }.time)
            onDateSelected(dateStr)
        },
        cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
    ).show()
}

@Preview(showBackground = true)
@Composable
fun AddHappyPlaceScreenPreview() {
    HappyPlacesTheme {
        AddHappyPlaceScreen(
            title = "title",
            onTitleChange = {},
            description = "description",
            onDescriptionChange = {},
            date = "date",
            onDateClick = {},
            location = "location",
            onLocationClick = {},
            onSelectCurrentLocation = {},
            imageUrl = null,
            onAddImageClick = {},
            onSaveClick = {},
            onBack = {},
            toolbarTitle = "Add Happy Place",
            buttonText = stringResource(R.string.btn_text_save)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MockAddHappyPlaceScreenPreview() {
    SetupPreviewKoin()
    val viewModel: HappyPlaceViewModel = koinViewModel()
    AddHappyPlaceScreen(id = "1", viewModel = viewModel) {}
}