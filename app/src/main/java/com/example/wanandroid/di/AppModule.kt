package com.example.wanandroid.di

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.example.wanandroid.data.local.AppDatabase
import com.example.wanandroid.data.local.BrowsingHistoryDao
import com.example.wanandroid.data.local.SearchHistoryDao
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    fun provideAccountManager(context: Context): AccountManager {
        return AccountManager.get(context)
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "wanandroid.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao {
        return db.searchHistoryDao()
    }

    @Provides
    fun provideBrowsingHistoryDao(db: AppDatabase): BrowsingHistoryDao {
        return db.browsingHistoryDao()
    }

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}