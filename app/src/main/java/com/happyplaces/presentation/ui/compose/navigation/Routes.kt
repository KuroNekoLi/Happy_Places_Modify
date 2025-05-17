package com.happyplaces.presentation.ui.compose.navigation

import kotlinx.serialization.Serializable

// onboarding
@Serializable
data object Onboarding

@Serializable
data object Email

@Serializable
data object Username

@Serializable
data object Profile

@Serializable
data object Welcome

// main
@Serializable
data object Home

@Serializable
data object Add

@Serializable
data class Edit(val id: String)

@Serializable
data class Detail(val id: String)

@Serializable
data class Map(val id: String)

@Serializable
data object Settings