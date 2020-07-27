package com.example.wanandroid.di

import com.example.wanandroid.data.remote.*
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val builder = Retrofit.Builder()
            .baseUrl("https://wanandroid.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
        return builder.build()
    }

    @Provides
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    fun provideArticleService(retrofit: Retrofit): ArticleService {
        return retrofit.create(ArticleService::class.java)
    }

    @Provides
    fun provideBannerService(retrofit: Retrofit): BannerService {
        return retrofit.create(BannerService::class.java)
    }

    @Provides
    fun provideCategoryService(retrofit: Retrofit): CategoryService {
        return retrofit.create(CategoryService::class.java)
    }

    @Provides
    fun provideSearchService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Provides
    fun provideToolService(retrofit: Retrofit): ToolService {
        return retrofit.create(ToolService::class.java)
    }

    @Provides
    fun provideTodoService(retrofit: Retrofit): TodoService {
        return retrofit.create(TodoService::class.java)
    }
}