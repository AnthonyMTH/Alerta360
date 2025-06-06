package com.unsa.alerta360.di

import com.unsa.alerta360.data.repository.AuthRepositoryImpl
import com.unsa.alerta360.data.repository.UserRepositoryImpl
import com.unsa.alerta360.domain.repository.AuthRepository
import com.unsa.alerta360.domain.repository.UserRepository
import com.unsa.alerta360.domain.usecase.auth.GetCurrentUserUseCase
import com.unsa.alerta360.domain.usecase.auth.LoginUserUseCase
import com.unsa.alerta360.domain.usecase.auth.RegisterUserUseCase
import com.unsa.alerta360.domain.usecase.user.SaveUserDetailsUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent // Para UseCases si son scoped a ViewModel
import dagger.hilt.android.scopes.ViewModelScoped // Para UseCases si son scoped a ViewModel
import dagger.hilt.components.SingletonComponent // Para Repositories
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class) // Repositories suelen ser Singletons
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firestore)
    }
}

@Module
@InstallIn(ViewModelComponent::class) // UseCases suelen tener el scope del ViewModel
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideLoginUserUseCase(authRepository: AuthRepository): LoginUserUseCase {
        return LoginUserUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideRegisterUserUseCase(
        authRepository: AuthRepository,
        userRepository: UserRepository
    ): RegisterUserUseCase {
        return RegisterUserUseCase(authRepository, userRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveUserDetailsUseCase(userRepository: UserRepository): SaveUserDetailsUseCase {
        return SaveUserDetailsUseCase(userRepository)
    }
}