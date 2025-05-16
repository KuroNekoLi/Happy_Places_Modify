package com.happyplaces.di

import com.happyplaces.domain.GetCurrentUserUseCase
import com.happyplaces.domain.UserUseCase
import org.koin.dsl.module

/** 使用案例層 */
val useCaseModule = module {
    factory<UserUseCase> { UserUseCase(get()) }
    factory<GetCurrentUserUseCase> { GetCurrentUserUseCase(get()) }
}