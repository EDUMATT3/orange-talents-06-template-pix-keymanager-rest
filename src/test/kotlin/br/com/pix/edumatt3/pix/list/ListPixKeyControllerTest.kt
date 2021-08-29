package br.com.pix.edumatt3.pix.list

import br.com.edumatt3.*
import br.com.edumatt3.ListPixKeyResponse
import br.com.pix.edumatt3.grpc.GrpcClientFactory
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class ListPixKeyControllerTest {

    @Inject
    lateinit var grpcClient: ListPixKeyServiceGrpc.ListPixKeyServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @AfterEach
    fun cleanUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    internal fun `should return a list of key`() {

        val clientId = UUID.randomUUID().toString()

        val grpcResponse = ListPixKeyResponse.newBuilder()
            .setClientId(clientId)
            .addAllPixKeys(listOf(
                ListPixKeyResponse.PixKey.newBuilder()
                    .setKey("50508675847")
                    .setKeyType(KeyType.CPF)
                    .setPixId(UUID.randomUUID().toString())
                    .setCreatedAt(Timestamp.newBuilder().build())
                    .build(),
                ListPixKeyResponse.PixKey.newBuilder()
                    .setKey("+5519981818181")
                    .setKeyType(KeyType.PHONE)
                    .setPixId(UUID.randomUUID().toString())
                    .setCreatedAt(Timestamp.newBuilder().build())
                    .build()
            )).build()

        BDDMockito.given(grpcClient.list(BDDMockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.GET<Any>("/api/pix/clients/$clientId/keys")

        val response = client.toBlocking().exchange(request, List::class.java)

        with(response){
            assertEquals(HttpStatus.OK, status)
            assertNotNull(body())
            assertTrue(body()!!.size == 2)
        }
    }

    @Test
    internal fun `should not return a pix key info when isnt found`() {

        val clientId = UUID.randomUUID().toString()
        val grpcResponse = ListPixKeyResponse.newBuilder()
            .setClientId(clientId)
            .build()

        BDDMockito.given(grpcClient.list(BDDMockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.GET<Any>("/api/pix/clients/$clientId/keys")


        val response = client.toBlocking().exchange(request, List::class.java)

        with(response){
            assertEquals(HttpStatus.OK, status)
            assertNotNull(body())
            assertTrue(body()!!.isEmpty())
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class ListKeyGrpcStub() {

        @Singleton
        internal fun getListService() = Mockito.mock(ListPixKeyServiceGrpc.ListPixKeyServiceBlockingStub::class.java)
    }
}