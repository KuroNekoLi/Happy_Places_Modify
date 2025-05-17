package com.happyplaces.di

import com.happyplaces.domain.usecase.GetCurrentUserUseCase
import com.happyplaces.domain.usecase.SignOutUseCase
import com.happyplaces.domain.usecase.UserUseCase
import org.koin.dsl.module

/** 使用案例層 */
val useCaseModule = module {
    factory<UserUseCase> { UserUseCase(get()) }
    factory<GetCurrentUserUseCase> { GetCurrentUserUseCase(get()) }
    factory { SignOutUseCase(get()) }
}