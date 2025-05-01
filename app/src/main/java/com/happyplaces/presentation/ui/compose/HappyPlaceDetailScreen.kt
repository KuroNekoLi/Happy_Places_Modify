package com.happyplaces.presentation.ui.compose

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.happyplaces.R
import com.happyplaces.presentation.HappyPlaceViewModel
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HappyPlaceDetailScreen(
    id:Int,
    viewModel: HappyPlaceViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onViewOnMapClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.getHappyPlaceById(id)
    }
    val happyPlace by viewModel.uiState.collectAsState()
    HappyPlaceDetailScreen(
        toolbarTitle = happyPlace.title,
        imageUri = happyPlace.imageUri,
        description = happyPlace.description,
        location = happyPlace.location,
        onBackClick = onBackClick,
        onViewOnMapClick = onViewOnMapClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HappyPlaceDetailScreen(
    toolbarTitle: String,
    imageUri: Uri?,
    description: String,
    location: String,
    onBackClick: () -> Unit,
    onViewOnMapClick: () -> Unit
) {
    Scaffold(
        topBar = {
            HappyPlaceToolBar(true, toolbarTitle, onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize(),
                placeholder = painterResource(id = R.drawable.detail_screen_image_placeholder),
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.detail_screen_description_margin)))

            // 描述文字
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.detail_screen_description_text_color),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.detail_screen_description_margin))
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.detail_screen_location_marginTop)))

            // 位置文字
            Text(
                text = location,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = colorResource(id = R.color.colorAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.detail_screen_location_marginStartEnd))
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.detail_screen_btn_view_on_map_marginTop)))

            // 「在地圖上查看」按鈕
            Button(
                onClick = onViewOnMapClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.detail_screen_btn_view_on_map_marginStartEnd))
                    .heightIn(min = dimensionResource(id = R.dimen.detail_screen_btn_view_on_map_PaddingTopBottom) * 2),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.btn_text_view_on_map),
                    style = MaterialTheme.typography.labelLarge,
                    color = colorResource(id = R.color.white_color)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HappyPlaceDetailScreenPreview() {
    HappyPlacesTheme {
        HappyPlaceDetailScreen(
            toolbarTitle = "Add Happy Place",
            imageUri = null,
            description = "Description",
            location = "Location",
            onBackClick = {},
            onViewOnMapClick = {}
        )
    }
}