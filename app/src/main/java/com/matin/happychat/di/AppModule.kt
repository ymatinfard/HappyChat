package com.matin.happychat.di

import com.matin.happychat.data.grpc.GrpcChatRepository
import com.matin.happychat.data.grpc.GrpcGrpcChatRepositoryImpl
import com.matin.happychat.data.grpc.GRPCClient
import com.matin.happychat.data.rest.MessageRepository
import com.matin.happychat.data.rest.MessageRepositoryImpl
import com.matin.happychat.mediaplayer.AudioFileStorage
import com.matin.happychat.mediaplayer.ExternalAudioFileStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {

    @Provides
    @Singleton
    fun provideChatRepository(grpcClient: GRPCClient): GrpcChatRepository {
        return GrpcGrpcChatRepositoryImpl(grpcClient)
    }
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