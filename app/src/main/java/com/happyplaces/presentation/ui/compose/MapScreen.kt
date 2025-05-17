package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.happyplaces.data.model.toHappyPlace
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.presentation.ui.compose.common.HappyPlaceToolBar
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapScreen(
    id: String,
    viewModel: HappyPlaceViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getHappyPlaceById(id)
    }
    val uiState by viewModel.uiState.collectAsState()
    uiState.toHappyPlace()?.let { place ->
        MapScreen(place = place, onBackClick = onBackClick, onInfoWindowClick = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    place: HappyPlace,
    onBackClick: () -> Unit,
    onInfoWindowClick: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(place.latitude, place.longitude),
            15f
        )
    }

    Scaffold(
        topBar = {
            HappyPlaceToolBar(true, place.title.orEmpty(), onBackClick)
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState
        ) {
            // 準備 MarkerState
            val markerState = rememberUpdatedMarkerState(
                position = LatLng(place.latitude, place.longitude)
            )

            // Marker Composable
            Marker(
                state = markerState,                                     // 必填
                contentDescription = null,    // 無障礙
                alpha = 1f,                                             // 預設不透明
                anchor = Offset(0.5f, 1f),                               // 圖片底部中央
                draggable = false,                                          // 不允許拖動
                flat = false,                                          // 非平貼
                icon = BitmapDescriptorFactory.defaultMarker(),       // 預設圖示
                infoWindowAnchor = Offset(0.5f, 0f),                               // 資訊窗頂部中央
                rotation = 0f,                                             // 無旋轉
                snippet = place.description,                              // 小標題
                tag = place.id,                                       // 自訂 tag
                title = place.location,                                 // 標題
                visible = true,                                           // 可見
                zIndex = 0f,                                             // 預設層級
                onClick = { marker ->                                   // 點擊回傳 Boolean 表示是否攔截
                    /* 如要顯示 InfoWindow 則回傳 false */
                    false
                },
                onInfoWindowClick = { _ -> onInfoWindowClick() },                   // 點 InfoWindow
                onInfoWindowClose = { /* 可選：InfoWindow 關閉時 */ },
                onInfoWindowLongClick = { /* 可選：長按 InfoWindow */ }
            )
        }
    }
}