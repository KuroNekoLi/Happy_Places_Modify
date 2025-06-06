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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.happyplaces.presentation.ui.compose.common.HappyPlaceToolBar
import com.happyplaces.presentation.ui.compose.common.ImagePickerSection
import com.happyplaces.presentation.ui.compose.common.ImageSourceDialog
import com.happyplaces.presentation.ui.compose.common.ValidatedTextField
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
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Image picker state
    var capturedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showImageDialog by remember { mutableStateOf(false) }

    // Initialize place if editing - only run when id changes
    LaunchedEffect(id) {
        id?.let { viewModel.getHappyPlaceById(it) }
    }

    // Initialize Places API - only run once
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.google_maps_api_key))
        }
    }

    // Activity Result Launchers
    val activityLaunchers = rememberActivityLaunchers(
        onImagePicked = viewModel::onImagePicked,
        onLocationSelected = viewModel::onLocationSelected,
        onLocationUpdate = viewModel::updateCurrentLatLng,
        onCapturedPhotoUri = { capturedPhotoUri = it },
        onShowImageDialog = { showImageDialog = it }
    )

    // Handle one-off events - key by the event to prevent duplicate handling
    val currentOnEventConsumed by rememberUpdatedState(newValue = viewModel::onEventConsumed)
    val currentOnBack by rememberUpdatedState(newValue = onBack)

    uiState.event?.let { event ->
        LaunchedEffect(event) {
            when (event) {
                AddPlaceEvent.ShowDatePicker -> showDatePicker(context, viewModel::onDateSelected)
                AddPlaceEvent.ShowImagePicker -> {
                    showImageDialog = true
                }

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
                    activityLaunchers.placeLauncher.launch(intent)
                }

                AddPlaceEvent.RequestCurrentLocation -> {
                    activityLaunchers.locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                is AddPlaceEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                AddPlaceEvent.NavigateBack -> currentOnBack()
                is AddPlaceEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }

                AddPlaceEvent.ShowLoading -> { /* Handled by UI state */
                }

                AddPlaceEvent.HideLoading -> { /* Handled by UI state */
                }
            }
            currentOnEventConsumed()
        }
    }

    // Image source dialog
    ImageSourceDialog(
        showDialog = showImageDialog,
        onDismiss = { showImageDialog = false },
        onGalleryClick = {
            activityLaunchers.pickMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onCameraClick = {
            activityLaunchers.cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    )

    // Main UI
    AddHappyPlaceContent(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onDateClick = viewModel::onDateClick,
        onLocationClick = viewModel::onLocationClick,
        onSelectCurrentLocation = viewModel::onSelectCurrentLocation,
        onAddImageClick = viewModel::onAddImageClick,
        onSaveClick = viewModel::onSaveClick,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHappyPlaceContent(
    uiState: com.happyplaces.data.model.AddPlaceUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onLocationClick: () -> Unit,
    onSelectCurrentLocation: () -> Unit,
    onAddImageClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit
) {
    val textFieldColor = TextFieldDefaults.colors().copy(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledIndicatorColor = MaterialTheme.colorScheme.outline,
        disabledContainerColor = Color.Transparent
    )

    Scaffold(
        topBar = {
            HappyPlaceToolBar(
                hasBack = true,
                toolbarTitle = stringResource(
                    if (uiState.isEditMode) R.string.edit_happy_place
                    else R.string.add_happy_place
                ),
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Field
            ValidatedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = stringResource(R.string.edit_text_hint_title),
                error = uiState.titleError
            )

            // Description Field
            ValidatedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = stringResource(R.string.edit_text_hint_description),
                error = uiState.descriptionError,
                singleLine = false
            )

            // Date Field (readonly)
            OutlinedTextField(
                value = if (uiState.date.isBlank()) "Date" else uiState.date,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDateClick() },
                colors = textFieldColor
            )

            // Location Field (readonly)
            OutlinedTextField(
                value = uiState.location,
                onValueChange = {},
                label = { Text(stringResource(R.string.edit_text_hint_location)) },
                readOnly = true,
                isError = uiState.locationError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLocationClick() },
                colors = textFieldColor
            )

            uiState.locationError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Current Location Button
            Text(
                text = stringResource(R.string.add_place_select_current_location_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectCurrentLocation() }
                    .padding(12.dp)
            )

            // Image Picker Section
            ImagePickerSection(
                imageUrl = uiState.imageUrl,
                onAddImageClick = onAddImageClick,
                error = uiState.imageError
            )

            // Save Button
            Button(
                onClick = onSaveClick,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = stringResource(
                        if (uiState.isEditMode) R.string.btn_text_update
                        else R.string.btn_text_save
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// Data class to hold all activity launchers
data class ActivityLaunchers(
    val pickMediaLauncher: androidx.activity.result.ActivityResultLauncher<PickVisualMediaRequest>,
    val cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    val cameraPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    val placeLauncher: androidx.activity.result.ActivityResultLauncher<Intent>,
    val locationPermissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>
)

@Composable
private fun rememberActivityLaunchers(
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
            // Get the captured photo URI from the remember state
            // This will be set by the cameraPermissionLauncher
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val capturedUri = buildFileUri(context)
            onCapturedPhotoUri(capturedUri)
            cameraLauncher.launch(capturedUri)
        } else {
            Toast.makeText(context, "相機權限被拒絕", Toast.LENGTH_SHORT).show()
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
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine && hasCoarse) {
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
                            result.lastLocation?.let { loc ->
                                onLocationUpdate(loc.latitude, loc.longitude)
                            }
                        }
                    },
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(context, "位置權限被拒絕", Toast.LENGTH_SHORT).show()
        }
    }

    return ActivityLaunchers(
        pickMediaLauncher,
        cameraLauncher,
        cameraPermissionLauncher,
        placeLauncher,
        locationPermissionLauncher
    )
}

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
fun AddHappyPlaceScreenPreviews() {
    HappyPlacesTheme {
        AddHappyPlaceContent(
            uiState = com.happyplaces.data.model.AddPlaceUiState(
                title = "Sample Title",
                description = "Sample Description",
                date = "2023.01.01",
                location = "Sample Location"
            ),
            onTitleChange = {},
            onDescriptionChange = {},
            onDateClick = {},
            onLocationClick = {},
            onSelectCurrentLocation = {},
            onAddImageClick = {},
            onSaveClick = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MockAddHappyPlaceScreenPreviews() {
    SetupPreviewKoin()
    val viewModel: HappyPlaceViewModel = koinViewModel()
    AddHappyPlaceScreen(id = "1", viewModel = viewModel) {}
}
