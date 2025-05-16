package com.matin.happychat.di

import android.content.Context
import androidx.room.Room
import com.matin.happychat.data.MessageRepositoryImpl
import com.matin.happychat.data.local.ChatDatabase
import com.matin.happychat.data.local.MessageDao
import com.matin.happychat.data.remote.rest.ChatApi
import com.matin.happychat.data.remote.rest.grpc.GRPCClient
import com.matin.happychat.data.remote.rest.grpc.GrpcChatRepository
import com.matin.happychat.data.remote.rest.grpc.GrpcGrpcChatRepositoryImpl
import com.matin.happychat.domain.MessageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {

    @Provides
    @Singleton
    fun provideChatRepository(grpcClient: GRPCClient): GrpcChatRepository {
        return GrpcGrpcChatRepositoryImpl(grpcClient)
    }

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext applicationContext: Context): ChatDatabase =
        Room.databaseBuilder(
            applicationContext,
            ChatDatabase::class.java,
            "chat_database"
        ).build()

    @Provides
    @Singleton
    fun provideMessageDao(chatDatabase: ChatDatabase): MessageDao = chatDatabase.messageDao()

    @Provides
    @IoDispatcher
    fun provideCoroutineIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideCoroutineMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideRetrofit(): ChatApi =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5005/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface AppBindsModule {

    @Binds
    @Singleton
    fun bindChatRepository(impl: MessageRepositoryImpl): MessageRepository
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher