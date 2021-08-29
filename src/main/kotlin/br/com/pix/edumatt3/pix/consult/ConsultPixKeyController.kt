package br.com.pix.edumatt3.pix.consult

import br.com.edumatt3.ConsultPixKeyRequest
import br.com.edumatt3.ConsultPixKeyServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.util.*

@Controller
class ConsultPixKeyController(private val clientGrpc: ConsultPixKeyServiceGrpc.ConsultPixKeyServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get("/clients/{clientId}/keys/{pixKeyId}")
    fun consult(clientId: UUID, pixKeyId: UUID): HttpResponse<Any> {

        logger.info("Consulting pix key with id: [$pixKeyId] from user: [$clientId]")

        val response = clientGrpc.consult(ConsultPixKeyRequest.newBuilder()
            .setPixId(ConsultPixKeyRequest.FilterByPixId.newBuilder()
                .setPixId(pixKeyId.toString())
                .setClientId(clientId.toString())
                .build())
            .build())

        return HttpResponse.ok(PixKeyDetailsResponse(response))
    }
}