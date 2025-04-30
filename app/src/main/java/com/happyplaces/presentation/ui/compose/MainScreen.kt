package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.happyplaces.data.datasource.local.HappyPlaceEntity
import com.happyplaces.data.model.mockHappyPlaceEntityLists
import com.happyplaces.presentation.HappyPlaceViewModel
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: HappyPlaceViewModel = koinViewModel(),
    onAddClick: () -> Unit = {},
    onEdit: (HappyPlaceEntity) -> Unit = {},
    onItemClick: (HappyPlaceEntity) -> Unit = {}
) {
    val list by viewModel.dataList.collectAsState()
    MainScreen(list = list, onAddClick = onAddClick, onEdit = onEdit, onDelete = viewModel::delete,onItemClick = onItemClick)
}

@Composable
fun MainScreen(
    list: List<HappyPlaceEntity>,
    onAddClick: () -> Unit,
    onDelete: (HappyPlaceEntity) -> Unit,
    onEdit: (HappyPlaceEntity) -> Unit,
    onItemClick: (HappyPlaceEntity) -> Unit
) {
    /** 觀察資料 */
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            HappyPlaceToolBar(false, stringResource(id = R.string.app_name))
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        /** 內容區域：LazyColumn or Empty State */
        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.note_text_no_happy_places_found_yes))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
}

@Preview(showBackground = true)
@Composable
fun MyScreenKoinPreview() {
    HappyPlacesTheme {
        MainScreen(
            list = mockHappyPlaceEntityLists,
            onAddClick = {},
            onDelete = {},
            onEdit = {},
            onItemClick = {}
        )
    }
}