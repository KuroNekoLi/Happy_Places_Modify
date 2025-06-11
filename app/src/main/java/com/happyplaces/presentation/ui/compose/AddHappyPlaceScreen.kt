package com.happyplaces.presentation.ui.compose

import android.Manifest
import android.net.Uri
import android.widget.Toast
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
import com.google.android.libraries.places.api.Places
import com.happyplaces.R
import com.happyplaces.data.model.AddPlaceEvent
import com.happyplaces.presentation.ui.compose.common.HappyPlaceToolBar
import com.happyplaces.presentation.ui.compose.common.ImagePickerSection
import com.happyplaces.presentation.ui.compose.common.ImageSourceDialog
import com.happyplaces.presentation.ui.compose.common.ValidatedTextField
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import com.happyplaces.util.ActivityLauncherHelper
import com.happyplaces.util.DatePickerUtils
import com.happyplaces.util.SetupPreviewKoin
import org.koin.androidx.compose.koinViewModel

/**
 * 新增或編輯快樂地點畫面
 * @param id 地點 ID，如果為 null 則為新增模式
 * @param viewModel ViewModel 實例
 * @param onBack 返回回調
 */
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
    val activityLaunchers = ActivityLauncherHelper.rememberActivityLaunchers(
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
                AddPlaceEvent.ShowDatePicker -> {
                    DatePickerUtils.showDatePicker(context, viewModel::onDateSelected)
                }
                
                AddPlaceEvent.ShowImagePicker -> {
                    showImageDialog = true
                }

                AddPlaceEvent.ShowPlacesAutocomplete -> {
                    ActivityLauncherHelper.showPlacesAutocomplete(context, activityLaunchers)
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

                AddPlaceEvent.ShowLoading,
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

/**
 * 新增快樂地點內容 UI
 * @param uiState UI 狀態
 * @param onTitleChange 標題變更回調
 * @param onDescriptionChange 描述變更回調
 * @param onDateClick 日期點擊回調
 * @param onLocationClick 位置點擊回調
 * @param onSelectCurrentLocation 選擇當前位置回調
 * @param onAddImageClick 新增圖片點擊回調
 * @param onSaveClick 儲存點擊回調
 * @param onBack 返回回調
 */
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
                enabled = false,
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
