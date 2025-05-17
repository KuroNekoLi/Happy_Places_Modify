package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.happyplaces.R
import com.happyplaces.data.model.mockHappyPlaceLists
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.presentation.ui.compose.common.happyPlaceItems
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.HappyPlaceViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecommendScreen(
    modifier: Modifier = Modifier,
    viewModel: HappyPlaceViewModel = koinViewModel(),
    onEdit: (HappyPlace) -> Unit = {},
    onItemClick: (HappyPlace) -> Unit = {}
) {
    //TODO: 根據狀態顯示
    val dataListApiResourceFlow by viewModel.allPlacesApiResourceFlow.collectAsState()
    val list = dataListApiResourceFlow.data ?: emptyList()
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
                isItemSwipeEnabled = false,
                onDelete = viewModel::delete,
                onEdit = onEdit,
                onItemClick = onItemClick
            )
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