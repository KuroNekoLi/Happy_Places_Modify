package com.happyplaces.presentation.ui.compose.profile

import android.Manifest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.happyplaces.presentation.ui.compose.common.Avatar
import com.happyplaces.presentation.ui.compose.common.HappyPlaceToolBar
import com.happyplaces.presentation.ui.compose.common.ImageSourceDialog
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.EditProfileViewModel
import com.happyplaces.util.ActivityLauncherHelper
import org.koin.androidx.compose.koinViewModel

/**
 * 編輯個人資料畫面
 * @param viewModel EditProfileViewModel 實例
 * @param onBack 返回回調
 */
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 圖片選擇對話框狀態
    var showImageDialog by remember { mutableStateOf(false) }

    // Activity 啟動器
    val activityLaunchers = ActivityLauncherHelper.rememberActivityLaunchers(
        onImagePicked = viewModel::onAvatarSelected,
        onLocationSelected = { _, _, _ -> }, // 不需要位置選擇
        onLocationUpdate = { _, _ -> }, // 不需要位置更新
        onCapturedPhotoUri = { },
        onShowImageDialog = { showImageDialog = it }
    )

    // 初始化用戶資料
    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    // 處理事件
    uiState.event?.let { event ->
        LaunchedEffect(event) {
            when (event) {
                is EditProfileViewModel.EditProfileEvent.ShowImagePicker -> {
                    showImageDialog = true
                }

                is EditProfileViewModel.EditProfileEvent.ShowMessage -> {
                    android.widget.Toast.makeText(
                        context,
                        event.message,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }

                EditProfileViewModel.EditProfileEvent.NavigateBack -> {
                    onBack()
                }
            }
            viewModel.onEventConsumed()
        }
    }

    // 圖片來源對話框
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

    EditProfileContent(
        uiState = uiState,
        onUsernameChange = viewModel::onUsernameChange,
        onBioChange = viewModel::onBioChange,
        onAvatarClick = viewModel::onAvatarClick,
        onSaveClick = viewModel::onSaveClick,
        onBack = onBack
    )
}

/**
 * 編輯個人資料內容 UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileContent(
    uiState: EditProfileViewModel.UiState,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            HappyPlaceToolBar(
                hasBack = true,
                toolbarTitle = "編輯個人資料",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 頭像區域
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Avatar(
                    imageUrl = uiState.avatarUrl,
                    size = 120.dp,
                    modifier = Modifier.clip(CircleShape)
                )

                OutlinedButton(
                    onClick = onAvatarClick
                ) {
                    Text("更換頭像")
                }
            }

            // 暱稱輸入欄位
            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                label = { Text("暱稱") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 個人簡介輸入欄位
            OutlinedTextField(
                value = uiState.bio,
                onValueChange = onBioChange,
                label = { Text("個人簡介") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // 帳號 ID 顯示（不可編輯）
            OutlinedTextField(
                value = uiState.accountId,
                onValueChange = {},
                label = { Text("帳號 ID") },
                enabled = false,
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 儲存按鈕
            Button(
                onClick = onSaveClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.isLoading) "儲存中..." else "儲存變更",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    HappyPlacesTheme {
        EditProfileContent(
            uiState = EditProfileViewModel.UiState(
                username = "測試用戶",
                bio = "這是我的個人簡介",
                accountId = "@testuser",
                avatarUrl = ""
            ),
            onUsernameChange = {},
            onBioChange = {},
            onAvatarClick = {},
            onSaveClick = {},
            onBack = {}
        )
    }
}