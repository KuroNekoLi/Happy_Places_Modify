package com.happyplaces.domain.usecase

import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.domain.repository.HappyPlaceRepository
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.flow.Flow

class GetMyPlacesUseCase(
    private val happyPlaceRepository: HappyPlaceRepository
) {
    fun invoke(): Flow<ApiResource<List<HappyPlace>>> = happyPlaceRepository.getMyPlaces()
}