package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.happyplaces.R
import com.happyplaces.data.model.mockHappyPlaceLists
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.presentation.HappyPlaceViewModel
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecommendScreen(
    modifier: Modifier = Modifier,
    viewModel: HappyPlaceViewModel = koinViewModel(),
    onEdit: (HappyPlace) -> Unit = {},
    onItemClick: (HappyPlace) -> Unit = {}
) {
    val dataListApiResourceFlow by viewModel.dataListApiResourceFlow.collectAsState()
    RecommendScreen(
        modifier = modifier,
        list = dataListApiResourceFlow.data ?: emptyList(),
        onEdit = onEdit,
        onDelete = viewModel::delete,
        onItemClick = onItemClick
    )
}

@Composable
fun RecommendScreen(
    modifier: Modifier = Modifier,
    list: List<HappyPlace>,
    onDelete: (HappyPlace) -> Unit,
    onEdit: (HappyPlace) -> Unit,
    onItemClick: (HappyPlace) -> Unit
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
            items(
                items = list,
                key = { it.id }
            ) { place ->
                SwipeableItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable(onClick = { onItemClick(place) }),
                    onDelete = { onDelete(place) },
                    onEdit = { onEdit(place) }
                ) {
                    HappyPlaceItem(
                        modifier = Modifier.fillMaxWidth(),
                        place = place,
                        onItemClick = onItemClick,
                    )
                }
            }
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