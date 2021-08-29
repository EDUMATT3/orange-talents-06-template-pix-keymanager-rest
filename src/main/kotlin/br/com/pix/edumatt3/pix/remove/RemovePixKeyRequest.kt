package br.com.pix.edumatt3.pix.remove

import br.com.edumatt3.DeletePixKeyRequest
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
class RemovePixKeyRequest(
    @field:NotNull val clientId: UUID
) {
    fun toGrpcRequest(pixKeyId: UUID): DeletePixKeyRequest {
        return DeletePixKeyRequest.newBuilder()
            .setClientId(clientId.toString())
            .setPixId(pixKeyId.toString())
            .build()
    }
}
