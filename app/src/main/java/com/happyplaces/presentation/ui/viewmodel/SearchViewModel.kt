package com.happyplaces.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.domain.repository.HappyPlaceRepository
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * 搜尋頁面的 ViewModel
 */
class SearchViewModel(
    private val repository: HappyPlaceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults =
        MutableStateFlow<ApiResource<List<HappyPlace>>>(ApiResource.Success(emptyList()))
    val searchResults: StateFlow<ApiResource<List<HappyPlace>>> = _searchResults.asStateFlow()

    private val _includeMyPlaces = MutableStateFlow(true)
    val includeMyPlaces: StateFlow<Boolean> = _includeMyPlaces.asStateFlow()

    /**
     * 更新搜尋關鍵字
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            performSearch(query)
        } else {
            _searchResults.value = ApiResource.Success(emptyList())
        }
    }

    /**
     * 切換是否包含自己的地點
     */
    fun toggleIncludeMyPlaces() {
        _includeMyPlaces.value = !_includeMyPlaces.value
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            performSearch(currentQuery)
        }
    }

    /**
     * 執行搜尋
     */
    private fun performSearch(query: String) {
        viewModelScope.launch {
            repository.searchHappyPlaces(query, _includeMyPlaces.value)
                .flowOn(Dispatchers.IO)
                .collect { result ->
                    _searchResults.value = result
                }
        }
    }

    /**
     * 清除搜尋結果
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = ApiResource.Success(emptyList())
    }

    /**
     * 刷新搜尋結果
     */
    fun refreshSearch() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            performSearch(currentQuery)
        }
    }
}