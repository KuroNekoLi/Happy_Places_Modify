package com.happyplaces.data.datasource.remote

import com.happyplaces.domain.model.User

data class UserDto(
    val id: String = "",
    val name: String = "",
    val accountID: String = "",
    val avatarUrl: String = "",
    val email: String = "",
    val bio: String = "",
    val createdAt: Long = 0L,
    val profileCompleted: Boolean = false,
    val isAnonymous: Boolean = false
)

fun UserDto.toUser(): User {
    return User(
        id = this.id,
        name = this.name,
        accountID = this.accountID,
        avatarUrl = this.avatarUrl,
        email = this.email,
        bio = this.bio,
        createdAt = this.createdAt,
        profileCompleted = this.profileCompleted,
        isAnonymous = this.isAnonymous
    )
}