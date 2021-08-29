package br.com.pix.edumatt3.pix.remove

import br.com.edumatt3.DeletePixKeyServiceGrpc
import br.com.pix.edumatt3.grpc.GrpcClientFactory
import com.google.protobuf.Empty
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class RemovePixKeyControllerTest {

    @Inject
    lateinit var grpcClient: DeletePixKeyServiceGrpc.DeletePixKeyServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @AfterEach
    fun cleanUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    internal fun `should delete a pix key`() {

        given(grpcClient.delete(Mockito.any())).willReturn(Empty.newBuilder().build())

        val request = HttpRequest.DELETE("/api/pix/key/${UUID.randomUUID().toString()}", RemovePixKeyRequest(UUID.randomUUID()))

        val status = client.toBlocking().exchange(request, Any::class.java).status

        assertEquals(HttpStatus.OK, status)
    }

    @Test
    internal fun `should return 502 when pix key dont belongs to the user`() {

        val exception = Status.PERMISSION_DENIED.withDescription("pix key doesn't belong to the customer")
        given(grpcClient.delete(Mockito.any())).willThrow(StatusRuntimeException(exception))

        val request = HttpRequest.DELETE("/api/pix/key/${UUID.randomUUID().toString()}", RemovePixKeyRequest(UUID.randomUUID()))

        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        with(thrown){
            assertEquals(HttpStatus.FORBIDDEN, status)
            assertFalse(response.headers.contains(HttpHeaders.LOCATION))
        }
    }

    @Test
    internal fun `should return 503 when pix key cant be deleted on bcb system`() {

        val exception = Status.FAILED_PRECONDITION.withDescription("pix key doesn't belong to the customer")
        given(grpcClient.delete(Mockito.any())).willThrow(StatusRuntimeException(exception))

        val request = HttpRequest.DELETE("/api/pix/key/${UUID.randomUUID().toString()}", RemovePixKeyRequest(UUID.randomUUID()))

        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        with(thrown){
            assertEquals(HttpStatus.BAD_GATEWAY, status)
            assertFalse(response.headers.contains(HttpHeaders.LOCATION))
            assertEquals("pix key doesn't belong to the customer", this.message)
        }
    }


    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class RemoveKeyGrpcStub() {

        @Singleton
        internal fun getRemoveService() = Mockito.mock(DeletePixKeyServiceGrpc.DeletePixKeyServiceBlockingStub::class.java)
    }
}