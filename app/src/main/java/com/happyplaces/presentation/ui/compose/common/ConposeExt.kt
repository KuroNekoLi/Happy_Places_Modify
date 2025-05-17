package com.happyplaces.presentation.ui.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.happyplaces.domain.model.HappyPlace

/**
 * 在 LazyColumn 或 LazyRow 的 content 區塊中調用，渲染 HappyPlace 列表項
 */
fun LazyListScope.happyPlaceItems(
    list: List<HappyPlace>,
    isItemSwipeEnabled: Boolean = true,
    onDelete: (HappyPlace) -> Unit = {},
    onEdit: (HappyPlace) -> Unit = {},
    onItemClick: (HappyPlace) -> Unit
) {
    items(items = list, key = { it.id }) { place ->
        val itemModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(place) }
        if (isItemSwipeEnabled) {
            SwipeableItem(
                modifier = itemModifier,
                onDelete = { onDelete(place) },
                onEdit = { onEdit(place) }
            ) {
                HappyPlaceItem(
                    modifier = Modifier.fillMaxWidth(),
                    place = place,
                    onItemClick = onItemClick
                )
            }
        } else {
            HappyPlaceItem(
                modifier = itemModifier,
                place = place,
                onItemClick = onItemClick
            )
        }
    }
}