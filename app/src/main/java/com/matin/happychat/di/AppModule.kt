package com.matin.happychat.di

import android.content.Context
import androidx.room.Room
import com.matin.happychat.data.MessageRepositoryImpl
import com.matin.happychat.data.grpc.GRPCClient
import com.matin.happychat.data.grpc.GrpcChatRepository
import com.matin.happychat.data.grpc.GrpcGrpcChatRepositoryImpl
import com.matin.happychat.data.local.ChatDatabase
import com.matin.happychat.data.local.MessageDao
import com.matin.happychat.domain.MessageRepository
import com.matin.happychat.mediaplayer.AudioFileStorage
import com.matin.happychat.mediaplayer.ExternalAudioFileStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    fun provideDb(@ApplicationContext applicationContext: Context): ChatDatabase = Room.databaseBuilder(
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
}

@Module
@InstallIn(SingletonComponent::class)
interface AppBindsModule {

    @Binds
    @Singleton
    fun bindAudioFileStorage(externalAudioFileStorage: ExternalAudioFileStorage): AudioFileStorage

    @Binds
    @Singleton
    fun bindMessageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher