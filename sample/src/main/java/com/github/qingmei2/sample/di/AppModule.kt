package com.github.qingmei2.sample.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.github.qingmei2.sample.BuildConfig
import com.github.qingmei2.sample.db.UserDatabase
import com.github.qingmei2.sample.http.interceptor.BasicAuthInterceptor
import com.github.qingmei2.sample.http.scheduler.SchedulerProvider
import com.github.qingmei2.sample.http.scheduler.SchedulerProviderProxy
import com.github.qingmei2.sample.http.service.LoginService
import com.github.qingmei2.sample.http.service.ServiceManager
import com.github.qingmei2.sample.http.service.UserService
import com.github.qingmei2.sample.repository.UserInfoRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val TIME_OUT_SECONDS = 10
const val BASE_URL = "https://api.github.com/"

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(userInfoRepository: UserInfoRepository): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(
                TIME_OUT_SECONDS.toLong(),
                TimeUnit.SECONDS
            )
            .readTimeout(
                TIME_OUT_SECONDS.toLong(),
                TimeUnit.SECONDS
            )
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = when (BuildConfig.DEBUG) {
                        true -> HttpLoggingInterceptor.Level.BODY
                        false -> HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .addInterceptor(BasicAuthInterceptor(userInfoRepository))
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDatabase(application: Application): UserDatabase {
        return Room.databaseBuilder(application, UserDatabase::class.java, "user")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return SchedulerProviderProxy()
    }

    @Singleton
    @Provides
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Singleton
    @Provides
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Singleton
    @Provides
    fun provideServiceManager(userService: UserService, loginService: LoginService): ServiceManager {
        return ServiceManager(
            userService = userService,
            loginService = loginService
        )
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("prefs_default", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideUserInfoRepository(sp: SharedPreferences): UserInfoRepository {
        return UserInfoRepository(sp)
    }
}