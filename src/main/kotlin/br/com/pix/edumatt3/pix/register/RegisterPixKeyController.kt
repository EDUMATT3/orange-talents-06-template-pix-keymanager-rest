package br.com.pix.edumatt3.pix.register

import br.com.edumatt3.CreatePixKeyServiceGrpc.CreatePixKeyServiceBlockingStub
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import javax.validation.Valid

@Validated
@Controller("/key")
class RegisterPixKeyController(@Inject val createKeyClient: CreatePixKeyServiceBlockingStub,) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Post
    fun register(@Valid @Body request: RegisterPixKeyRequest,): HttpResponse<Any> {

        logger.info("[${request.clientId}] registering new pix key with type: ${request.type}")

        val response = createKeyClient.register(request.toGrpcRequest())

        val location = HttpResponse.uri("/api/pix/key/${response.pixId}")
        return HttpResponse.created(location)
    }
}