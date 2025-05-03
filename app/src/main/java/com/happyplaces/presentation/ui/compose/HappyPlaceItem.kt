package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.happyplaces.data.model.HappyPlace
import com.happyplaces.data.model.mockHappyPlaceLists
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme

@Composable
fun HappyPlaceItem(
    modifier: Modifier = Modifier,
    place: HappyPlace,
    onItemClick: (HappyPlace) -> Unit
) =
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        val context = LocalContext.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    onItemClick(place)
                }
        ) {
            // 圓形圖片
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(place.image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(place.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
                Text(
                    place.description.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun HappyPlaceItemPreview() {
    HappyPlacesTheme {
        HappyPlaceItem(
            place = mockHappyPlaceLists.first(),
            onItemClick = {}
        )
    }
}