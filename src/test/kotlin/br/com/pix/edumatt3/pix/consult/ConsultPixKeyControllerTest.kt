package br.com.pix.edumatt3.pix.consult

import br.com.edumatt3.*
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
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class ConsultPixKeyControllerTest{

    @Inject
    lateinit var grpcClient: ConsultPixKeyServiceGrpc.ConsultPixKeyServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @AfterEach
    fun cleanUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    internal fun `should return a pix key info`() {

        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val grpcRequest = ConsultPixKeyResponse
            .newBuilder()
            .setClientId(clientId)
            .setPixId(pixId)
            .setPixKey(ConsultPixKeyResponse.PixKey.newBuilder()
                .setKey("50508675847")
                .setType(KeyType.EMAIL)
                .setCreatedAt(Timestamp.newBuilder().build())
                .setAccount(ConsultPixKeyResponse.PixKey.AccountInfo.newBuilder()
                    .setType(AccountType.CONTA_CORRENTE)
                    .setAgency("0001")
                    .setInstituition("ITAU UNIBANCO  S.A")
                    .setCustomerCpf("50508675847")
                    .setCustomerName("Fulano")
                ).build()
            ).build()

        given(grpcClient.consult(any())).willReturn(grpcRequest)

        val request = HttpRequest.GET<Any>("/api/pix/clients/$clientId/keys/$pixId")

        val response = client.toBlocking().exchange(request, Any::class.java)

        with(response){
            assertEquals(HttpStatus.OK, status)
            assertNotNull(body())
        }
    }

    @Test
    internal fun `should not return a pix key info when isnt found`() {

        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        given(grpcClient.consult(any())).willThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = HttpRequest.GET<Any>("/api/pix/clients/$clientId/keys/$pixId")

        val thrown = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        with(thrown){
            assertEquals(HttpStatus.NOT_FOUND, status)
            assertEquals(null, response.body())
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class ConsultKeyGrpcStub() {

        @Singleton
        internal fun getConsultService() = Mockito.mock(ConsultPixKeyServiceGrpc.ConsultPixKeyServiceBlockingStub::class.java)
    }
}