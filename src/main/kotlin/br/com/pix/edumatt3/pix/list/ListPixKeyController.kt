package br.com.pix.edumatt3.pix.list

import br.com.edumatt3.ListPixKeyRequest
import br.com.edumatt3.ListPixKeyServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.util.*

@Controller("clients")
class ListPixKeyController(val grpcClient: ListPixKeyServiceGrpc.ListPixKeyServiceBlockingStub) {

    @Get("{clientId}/keys")
    fun list(clientId: UUID): MutableHttpResponse<Any> {

        val response = grpcClient.list(ListPixKeyRequest.newBuilder()
            .setClientId(clientId.toString())
            .build())

        return HttpResponse.ok(response.pixKeysList.map(::PixKeyResponse))
    }

}