package com.stebitto.feature_camera_feed.di

import com.stebitto.feature_camera_feed.data.ColorNameRemoteSource
import com.stebitto.feature_camera_feed.data.ColorNameRemoteSourceImpl
import com.stebitto.feature_camera_feed.data.ColorNameRepository
import com.stebitto.feature_camera_feed.data.ColorNameRepositoryImpl
import com.stebitto.feature_camera_feed.data.GetTargetAreaColorUseCase
import com.stebitto.feature_camera_feed.data.GetTargetAreaColorUseCaseImpl
import com.stebitto.feature_camera_feed.data.retrofit.ColorAPIService
import com.stebitto.feature_camera_feed.presentation.CameraFeedViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { provideHttpClient() }
    single { provideConverterFactory() }
    single { provideRetrofit(get(), get()) }
    single { provideService(get()) }
}

val cameraFeedModule = module {
    viewModel { CameraFeedViewModel(get()) }
    factory<GetTargetAreaColorUseCase> { GetTargetAreaColorUseCaseImpl(get()) }
    factory<ColorNameRepository> { ColorNameRepositoryImpl(get()) }
    factory<ColorNameRemoteSource> { ColorNameRemoteSourceImpl(get()) }
    includes(networkModule)
}

internal fun provideHttpClient(): OkHttpClient {
    return OkHttpClient
        .Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
        )
        .build()
}

internal fun provideConverterFactory(): GsonConverterFactory =
    GsonConverterFactory.create()

internal fun provideRetrofit(
    okHttpClient: OkHttpClient,
    gsonConverterFactory: GsonConverterFactory
): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://www.thecolorapi.com/")
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()
}

internal fun provideService(retrofit: Retrofit): ColorAPIService =
    retrofit.create(ColorAPIService::class.java)