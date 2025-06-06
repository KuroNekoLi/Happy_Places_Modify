package com.happyplaces.presentation.ui.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.happyplaces.domain.model.HappyPlace

/**
 * 在 LazyColumn 或 LazyRow 的 contents 區塊中調用，渲染 HappyPlace 列表項
 * 優化版本：使用 key 參數避免不必要的重組，使用 contentType 優化性能
 */
fun LazyListScope.happyPlaceItems(
    list: List<HappyPlace>,
    isItemSwipeEnabled: Boolean = true,
    onDelete: (HappyPlace) -> Unit = {},
    onEdit: (HappyPlace) -> Unit = {},
    onItemClick: (HappyPlace) -> Unit
) {
    items(
        items = list,
        key = { it.id }, // 使用 stable key 避免不必要的重組
        contentType = { "happy_place_item" } // 提供 contentType 優化性能
    ) { place ->
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
                OptimizedHappyPlaceItem(
                    modifier = Modifier.fillMaxWidth(),
                    place = place,
                    onItemClick = onItemClick
                )
            }
        } else {
            OptimizedHappyPlaceItem(
                modifier = itemModifier,
                place = place,
                onItemClick = onItemClick
            )
        }
    }
}

/**
 * 優化的 HappyPlaceItem，使用 @Stable 註解和記憶化的回調函數
 */
@Stable
@androidx.compose.runtime.Composable
private fun OptimizedHappyPlaceItem(
    modifier: Modifier = Modifier,
    place: HappyPlace,
    onItemClick: (HappyPlace) -> Unit
) {
    // 使用 remember 來穩定回調函數引用
    val stableOnClick = androidx.compose.runtime.remember(place.id) {
        { onItemClick(place) }
    }

    HappyPlaceItem(
        modifier = modifier,
        place = place,
        onItemClick = { stableOnClick() }
    )
}
