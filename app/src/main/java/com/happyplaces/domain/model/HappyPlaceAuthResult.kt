package com.happyplaces.domain.model

data class HappyPlaceAuthResult(
    val user: HappyPlaceUser,
    val isNewUser: Boolean
)
