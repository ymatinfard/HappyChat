package com.matin.happychat.di

import com.matin.happychat.data.grpc.GrpcChatRepository
import com.matin.happychat.data.grpc.GrpcGrpcChatRepositoryImpl
import com.matin.happychat.data.grpc.GRPCClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideChatRepository(grpcClient: GRPCClient): GrpcChatRepository {
        return GrpcGrpcChatRepositoryImpl(grpcClient)
    }
}