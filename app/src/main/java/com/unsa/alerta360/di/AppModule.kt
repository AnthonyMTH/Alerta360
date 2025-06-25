package com.unsa.alerta360.di

import com.unsa.alerta360.data.repository.AuthRepositoryImpl
import com.unsa.alerta360.data.repository.UserRepositoryImpl
import com.unsa.alerta360.domain.repository.AuthRepository
import com.unsa.alerta360.domain.repository.UserRepository
import com.unsa.alerta360.domain.usecase.auth.GetCurrentUserUseCase
import com.unsa.alerta360.domain.usecase.auth.LoginUserUseCase
import com.unsa.alerta360.domain.usecase.auth.RegisterUserUseCase
import com.unsa.alerta360.domain.usecase.user.SaveUserDetailsUseCase
import com.unsa.alerta360.domain.usecase.user.GetUserDetailsUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unsa.alerta360.data.network.AccountApiService
import com.unsa.alerta360.data.network.IncidentApi
import com.unsa.alerta360.data.repository.AccountRepositoryImpl
import com.unsa.alerta360.data.repository.IncidentRepositoryImpl
import com.unsa.alerta360.data.repository.HybridIncidentRepositoryImpl
import com.unsa.alerta360.domain.repository.AccountRepository
import com.unsa.alerta360.domain.repository.IncidentRepository
import com.unsa.alerta360.domain.usecase.account.GetAccountDetailsUseCase
import com.unsa.alerta360.domain.usecase.incident.CreateIncidentUseCase
import com.unsa.alerta360.domain.usecase.incident.GetAllIncidentsUseCase
import com.unsa.alerta360.domain.usecase.incident.GetIncidentUseCase
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.sync.SyncManager
import com.unsa.alerta360.data.network.util.NetworkUtil
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
    fun provideAccountRepository(apiService: AccountApiService): AccountRepository {
        return AccountRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        accountApiService: AccountApiService
    ): UserRepository {
        return UserRepositoryImpl(firestore, accountApiService)
    }
    
    @Provides
    @Singleton
    fun provideIncidentRepository(
        incidentDao: IncidentDao,
        api: IncidentApi,
        syncManager: SyncManager,
        networkUtil: NetworkUtil
    ): IncidentRepository {
        return HybridIncidentRepositoryImpl(incidentDao, api, syncManager, networkUtil)
    }
    
    @Provides
    @Singleton
    fun provideSyncManager(
        incidentDao: IncidentDao,
        incidentApi: IncidentApi,
        networkUtil: NetworkUtil
    ): SyncManager {
        return SyncManager(incidentDao, incidentApi, networkUtil)
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

    @Provides
    @ViewModelScoped
    fun provideGetUserDetailsUseCase(userRepository: UserRepository): GetUserDetailsUseCase {
        return GetUserDetailsUseCase(userRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateIncidentUseCase(incidentRepository: IncidentRepository): CreateIncidentUseCase {
        return CreateIncidentUseCase(incidentRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllIncidentsUseCase(incidentRepository: IncidentRepository): GetAllIncidentsUseCase {
        return GetAllIncidentsUseCase(incidentRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAccountDetailsUseCase(accountRepository: AccountRepository): GetAccountDetailsUseCase {
        return GetAccountDetailsUseCase(accountRepository)
    }
    @Provides
    @ViewModelScoped
    fun provideGetIncidentUseCase(
        incidentRepository: IncidentRepository
    ): GetIncidentUseCase {
        return GetIncidentUseCase(incidentRepository)
    }
}