package com.happyplaces.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.happyplaces.R
import com.happyplaces.data.model.mockHappyPlaceLists
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.presentation.ui.compose.common.happyPlaceItems
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.SearchViewModel
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 搜尋頁面元件
 *
 * @param modifier 修飾符
 * @param viewModel 搜尋 ViewModel
 * @param onEdit 編輯地點的回調函數
 * @param onItemClick 點擊地點項目的回調函數
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    onEdit: (HappyPlace) -> Unit = {},
    onItemClick: (HappyPlace) -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val includeMyPlaces by viewModel.includeMyPlaces.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // 監聽搜尋結果狀態變化，當不是 Loading 時停止刷新
    LaunchedEffect(searchResults) {
        if (searchResults !is ApiResource.Loading) {
            isRefreshing = false
        }
    }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            viewModel.refreshSearch()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 搜尋輸入框
        SearchInputSection(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onClearSearch = viewModel::clearSearch
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 搜尋選項
        SearchOptionsSection(
            includeMyPlaces = includeMyPlaces,
            onToggleIncludeMyPlaces = viewModel::toggleIncludeMyPlaces
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 搜尋結果
        SearchResultsSection(
            modifier = Modifier.weight(1f),
            searchResults = searchResults,
            searchQuery = searchQuery,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            onEdit = onEdit,
            onItemClick = onItemClick
        )
    }
}

/**
 * 搜尋輸入區塊
 */
@Composable
private fun SearchInputSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.bottom_nav_search)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.search_clear)
                    )
                }
            }
        },
        singleLine = true
    )
}

/**
 * 搜尋選項區塊
 */
@Composable
private fun SearchOptionsSection(
    includeMyPlaces: Boolean,
    onToggleIncludeMyPlaces: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeMyPlaces,
                onCheckedChange = { onToggleIncludeMyPlaces() }
            )
            Text(
                text = stringResource(R.string.include_my_places),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/**
 * 搜尋結果區塊
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultsSection(
    modifier: Modifier = Modifier,
    searchResults: ApiResource<List<HappyPlace>>,
    searchQuery: String,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onEdit: (HappyPlace) -> Unit,
    onItemClick: (HappyPlace) -> Unit
) {
    when (searchResults) {
        is ApiResource.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ApiResource.Success -> {
            val results = searchResults.data ?: emptyList()

            if (searchQuery.isBlank()) {
                // 沒有輸入搜尋關鍵字
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.search_empty_hint),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (results.isEmpty()) {
                // 沒有搜尋結果
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.search_no_results),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // 有搜尋結果
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = modifier.fillMaxSize()
                ) {
                    LazyColumn {
                        happyPlaceItems(
                            list = results,
                            isItemSwipeEnabled = false,
                            onDelete = { /* 搜尋頁面不允許刪除 */ },
                            onEdit = onEdit,
                            onItemClick = onItemClick
                        )
                    }
                }
            }
        }

        is ApiResource.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.search_failed),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = searchResults.message ?: stringResource(R.string.unknown_error),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "搜尋頁面預覽")
@Composable
fun SearchScreenPreview() {
    HappyPlacesTheme {
        SearchScreen()
    }
}

@Preview(showBackground = true, name = "搜尋結果預覽")
@Composable
fun SearchResultsPreview() {
    HappyPlacesTheme {
        SearchResultsSection(
            searchResults = ApiResource.Success(mockHappyPlaceLists),
            searchQuery = "test",
            isRefreshing = false,
            onRefresh = {},
            onEdit = {},
            onItemClick = {}
        )
    }
}
