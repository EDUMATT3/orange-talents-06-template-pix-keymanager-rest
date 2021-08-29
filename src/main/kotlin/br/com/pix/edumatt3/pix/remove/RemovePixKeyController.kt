package br.com.pix.edumatt3.pix.remove

import br.com.edumatt3.DeletePixKeyServiceGrpc.DeletePixKeyServiceBlockingStub
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.Valid

@Validated
@Controller("/keys")
class RemovePixKeyController(val grpcClient: DeletePixKeyServiceBlockingStub) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Delete("/{pixKeyId}")
    fun remove(pixKeyId: UUID, @Valid @Body request: RemovePixKeyRequest) : HttpResponse<Any> {

        logger.info("Deleting pix key: [$pixKeyId] from user: [${request.clientId}]")

        val grpcRequest = request.toGrpcRequest(pixKeyId)

        grpcClient.delete(grpcRequest)

        return HttpResponse.ok()
    }
}