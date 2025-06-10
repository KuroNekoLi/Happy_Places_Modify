package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.ProfileViewModel
import com.happyplaces.util.ApiResource
import org.koin.androidx.compose.koinViewModel

/**
 * 我的地圖頁面元件，顯示所有我的地點在地圖上
 *
 * @param modifier 修飾符
 * @param viewModel Profile ViewModel（包含我的地點資料）
 * @param onMarkerClick 點擊地圖標記的回調函數
 */
@Composable
fun MyMapScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
    onMarkerClick: (HappyPlace) -> Unit = {}
) {
    val myPlacesApiResource by viewModel.myPlaces.collectAsState()

    when (myPlacesApiResource) {
        is ApiResource.Loading -> {
            LoadingScreen(modifier = modifier)
        }

        is ApiResource.Success -> {
            val places = myPlacesApiResource.data ?: emptyList()
            if (places.isEmpty()) {
                EmptyMapScreen(modifier = modifier)
            } else {
                MapWithMarkersScreen(
                    modifier = modifier,
                    places = places,
                    onMarkerClick = onMarkerClick
                )
            }
        }

        is ApiResource.Error -> {
            ErrorMapScreen(
                modifier = modifier,
                errorMessage = myPlacesApiResource.message ?: "載入地點失敗"
            )
        }
    }
}

/**
 * 載入中畫面
 */
@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(
                text = "載入地點中...",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

/**
 * 空地圖畫面（沒有地點時）
 */
@Composable
private fun EmptyMapScreen(modifier: Modifier = Modifier) {
    val defaultPosition = LatLng(25.0330, 121.5654) // 台北101
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = false
            )
        )

        // 空狀態提示卡片，使用半透明背景
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = "還沒有任何地點\n去新增你的第一個快樂地點吧！",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

/**
 * 有地點標記的地圖畫面
 */
@Composable
private fun MapWithMarkersScreen(
    modifier: Modifier = Modifier,
    places: List<HappyPlace>,
    onMarkerClick: (HappyPlace) -> Unit
) {
    // 計算地圖中心點（所有地點的平均位置）
    val centerLat = places.map { it.latitude }.average()
    val centerLng = places.map { it.longitude }.average()
    val centerPosition = LatLng(centerLat, centerLng)

    // 計算適當的縮放級別
    val latitudes = places.map { it.latitude }
    val longitudes = places.map { it.longitude }
    val latSpan = (latitudes.maxOrNull() ?: 0.0) - (latitudes.minOrNull() ?: 0.0)
    val lngSpan = (longitudes.maxOrNull() ?: 0.0) - (longitudes.minOrNull() ?: 0.0)
    val maxSpan = maxOf(latSpan, lngSpan)

    // 根據跨度決定縮放級別
    val zoomLevel = when {
        maxSpan > 10 -> 5f
        maxSpan > 5 -> 7f
        maxSpan > 1 -> 10f
        maxSpan > 0.1 -> 12f
        else -> 15f
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerPosition, zoomLevel)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = false
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = false
        )
    ) {
        // 為每個地點添加標記
        places.forEach { place ->
            Marker(
                state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                title = place.title,
                snippet = place.description,
                onClick = {
                    onMarkerClick(place)
                    true
                }
            )
        }
    }
}

/**
 * 錯誤畫面
 */
@Composable
private fun ErrorMapScreen(
    modifier: Modifier = Modifier,
    errorMessage: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "載入地圖失敗",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "空地圖預覽")
@Composable
fun EmptyMapScreenPreview() {
    HappyPlacesTheme {
        EmptyMapScreen()
    }
}

@Preview(showBackground = true, name = "載入中預覽")
@Composable
fun LoadingMapScreenPreview() {
    HappyPlacesTheme {
        LoadingScreen()
    }
}

/**
 * 空狀態卡片預覽（獨立元件）
 */
@Preview(showBackground = true, name = "空狀態卡片預覽")
@Composable
fun EmptyStateCardPreview() {
    HappyPlacesTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "還沒有任何地點\n去新增你的第一個快樂地點吧！",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}
