package br.com.pix.edumatt3.pix

import br.com.edumatt3.CreatePixKeyServiceGrpc.CreatePixKeyServiceBlockingStub
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/key")
class RegisterPixKeyController(@Inject val createKeyClient: CreatePixKeyServiceBlockingStub,) {

    @Post
    fun register(@Valid @Body request: RegisterPixKeyRequest,): HttpResponse<Any> {
            val response = createKeyClient.register(request.toGrpcRequest())
            val uriLocation = UriBuilder.of("/key/{id}")
                .expand(mutableMapOf("id" to response.pixId))

            return HttpResponse.created(uriLocation)
    }
}