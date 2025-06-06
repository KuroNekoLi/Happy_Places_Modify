package com.happyplaces.presentation.ui.compose.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.happyplaces.R

@Composable
fun Avatar(
    imageUrl: String?,
    size: Dp = 64.dp
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val data = if (isPreview) {
        R.drawable.icon
    } else {
        imageUrl
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .build(),
        placeholder = painterResource(id = R.drawable.add_screen_image_placeholder),
        error = painterResource(id = R.drawable.add_screen_image_placeholder),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
fun AvatarPreview() {
    Avatar(
        imageUrl = "https://picsum.photos/200/300",
        size = 40.dp
    )
}
