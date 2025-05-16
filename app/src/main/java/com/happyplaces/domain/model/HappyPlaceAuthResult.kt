package com.happyplaces.domain.model

data class HappyPlaceAuthResult(
    val user: AuthUser,
    val isNewUser: Boolean
)
