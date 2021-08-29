package br.com.pix.edumatt3.pix

import br.com.edumatt3.AccountType
import br.com.edumatt3.CreatePixKeyResponse
import br.com.edumatt3.CreatePixKeyServiceGrpc
import br.com.edumatt3.KeyType
import br.com.pix.edumatt3.grpc.GrpcClientFactory
import br.com.pix.edumatt3.pix.register.RegisterPixKeyRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class RegisterPixKeyControllerTest {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Inject
    lateinit var createGrpcStub: CreatePixKeyServiceGrpc.CreatePixKeyServiceBlockingStub

    @Test
    internal fun `should register a valid new key`() {

        val pixId = UUID.randomUUID().toString()
        val grpcResponse = CreatePixKeyResponse.newBuilder().setPixId(pixId).build()

        given(createGrpcStub.register(Mockito.any())).willReturn(grpcResponse)

        val requestBody = RegisterPixKeyRequest(
            clientId = UUID.randomUUID().toString(),
            type = KeyType.CPF,
            key = "50508675847",
            accountType = AccountType.CONTA_CORRENTE
        )

        val request = HttpRequest.POST("/api/pix/key", requestBody)
        val response = client.toBlocking().exchange(request, Any::class.java)

        with(response){
            assertEquals(HttpStatus.CREATED, status)
            assertTrue(headers.contains(HttpHeaders.LOCATION))
            assertTrue(header(HttpHeaders.LOCATION).contains(pixId))
        }
    }

    @Test
    internal fun `should return 422 when key already exists`() {
        val grpcResponse = StatusRuntimeException(Status.ALREADY_EXISTS.withDescription("Pix key already exists"))

        given(createGrpcStub.register(Mockito.any())).willThrow(grpcResponse)

        val requestBody = RegisterPixKeyRequest(
            clientId = UUID.randomUUID().toString(),
            type = KeyType.CPF,
            key = "50508675847",
            accountType = AccountType.CONTA_CORRENTE
        )

        val request = HttpRequest.POST("/api/pix/key", requestBody)
        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        with(thrown){
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, status)
            assertFalse(response.headers.contains(HttpHeaders.LOCATION))
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class GrpcClientStub{

        @Singleton
        internal fun createPixKey() = Mockito.mock(CreatePixKeyServiceGrpc.CreatePixKeyServiceBlockingStub::class.java)
    }
}