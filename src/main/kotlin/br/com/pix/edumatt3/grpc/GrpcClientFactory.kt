package br.com.pix.edumatt3.grpc

import br.com.edumatt3.ConsultPixKeyServiceGrpc
import br.com.edumatt3.CreatePixKeyServiceGrpc
import br.com.edumatt3.DeletePixKeyServiceGrpc
import br.com.edumatt3.ListPixKeyServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun createPixKey() = CreatePixKeyServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removePixKey() = DeletePixKeyServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun consultPixKey() = ConsultPixKeyServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listPixKey() = ListPixKeyServiceGrpc.newBlockingStub(channel)
}