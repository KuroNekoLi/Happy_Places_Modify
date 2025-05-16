package com.happyplaces.domain.model

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val image: String,
    val isAnonymous: Boolean
) {
    constructor() : this("", "", "", "", false)
}