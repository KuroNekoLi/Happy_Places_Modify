package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.happyplaces.R
import com.happyplaces.data.model.mockHappyPlaceLists
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.presentation.ui.compose.common.happyPlaceItems
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import com.happyplaces.util.ApiResource
import org.koin.androidx.compose.koinViewModel

/**
 * 推薦頁面元件，根據 ApiResource 狀態顯示不同 UI
 *
 * @param modifier 修飾符
 * @param viewModel 快樂地點 ViewModel
 * @param onEdit 編輯地點的回調函數
 * @param onItemClick 點擊地點項目的回調函數
 */
@Composable
fun RecommendScreen(
    modifier: Modifier = Modifier,
    viewModel: HappyPlaceViewModel = koinViewModel(),
    onEdit: (HappyPlace) -> Unit = {},
    onItemClick: (HappyPlace) -> Unit = {}
) {
    val dataListApiResource by viewModel.allPlacesApiResourceFlow.collectAsState()

    when (dataListApiResource) {
        is ApiResource.Loading -> {
            LoadingScreen(modifier = modifier)
        }

        is ApiResource.Success -> {
            val list = dataListApiResource.data ?: emptyList()
            SuccessScreen(
                modifier = modifier,
                list = list,
                onEdit = onEdit,
                onItemClick = onItemClick,
                onDelete = viewModel::delete
            )
        }

        is ApiResource.Error -> {
            ErrorScreen(
                modifier = modifier,
                errorMessage = dataListApiResource.message ?: "發生未知錯誤",
                onRetry = {
                    viewModel.updateAllHappyPlaces()
                }
            )
        }
    }
}

/**
 * 載入中畫面元件
 */
@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * 成功狀態畫面元件
 */
@Composable
private fun SuccessScreen(
    modifier: Modifier = Modifier,
    list: List<HappyPlace>,
    onEdit: (HappyPlace) -> Unit,
    onItemClick: (HappyPlace) -> Unit,
    onDelete: (HappyPlace) -> Unit
) {
    if (list.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.note_text_no_happy_places_found_yes),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            happyPlaceItems(
                list = list,
                isItemSwipeEnabled = false,
                onDelete = onDelete,
                onEdit = onEdit,
                onItemClick = onItemClick
            )
        }
    }
}

/**
 * 錯誤狀態畫面元件
 */
@Composable
private fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "載入失敗",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("重新整理")
            }
        }
    }
}

@Composable
fun RecommendScreen(
    modifier: Modifier = Modifier,
    list: List<HappyPlace>,
    onDelete: (HappyPlace) -> Unit,
    onEdit: (HappyPlace) -> Unit,
    onItemClick: (HappyPlace) -> Unit,
    isItemSwipeEnabled: Boolean = true
) {
    if (list.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.note_text_no_happy_places_found_yes))
        }
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            happyPlaceItems(
                list = list,
                isItemSwipeEnabled = isItemSwipeEnabled,
                onDelete = onDelete,
                onEdit = onEdit,
                onItemClick = onItemClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecommendScreenPreview() {
    HappyPlacesTheme {
        RecommendScreen(
            list = mockHappyPlaceLists,
            onDelete = {},
            onEdit = {},
            onItemClick = {}
        )
    }
}

/**
 * 載入中狀態預覽
 */
@Preview(showBackground = true, name = "載入中狀態")
@Composable
fun LoadingScreenPreview() {
    HappyPlacesTheme {
        LoadingScreen()
    }
}

/**
 * 成功狀態（有資料）預覽
 */
@Preview(showBackground = true, name = "成功狀態 - 有資料")
@Composable
fun SuccessScreenWithDataPreview() {
    HappyPlacesTheme {
        SuccessScreen(
            list = mockHappyPlaceLists,
            onEdit = {},
            onItemClick = {},
            onDelete = {}
        )
    }
}

/**
 * 成功狀態（無資料）預覽
 */
@Preview(showBackground = true, name = "成功狀態 - 無資料")
@Composable
fun SuccessScreenEmptyPreview() {
    HappyPlacesTheme {
        SuccessScreen(
            list = emptyList(),
            onEdit = {},
            onItemClick = {},
            onDelete = {}
        )
    }
}

/**
 * 錯誤狀態預覽
 */
@Preview(showBackground = true, name = "錯誤狀態")
@Composable
fun ErrorScreenPreview() {
    HappyPlacesTheme {
        ErrorScreen(
            errorMessage = "網路連線失敗，請檢查網路設定後重試",
            onRetry = {}
        )
    }
}

/**
 * 錯誤狀態預覽（短錯誤訊息）
 */
@Preview(showBackground = true, name = "錯誤狀態 - 短訊息")
@Composable
fun ErrorScreenShortMessagePreview() {
    HappyPlacesTheme {
        ErrorScreen(
            errorMessage = "載入失敗",
            onRetry = {}
        )
    }
}
