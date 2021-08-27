package br.com.pix.edumatt3.pix

import br.com.edumatt3.AccountType
import br.com.edumatt3.CreatePixKeyRequest
import br.com.edumatt3.KeyType
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
class RegisterPixKeyRequest(
    @field:NotBlank val clientId: String,
    @field:NotNull val type: KeyType,
    @field:Max(77) val key: String?,
    @field:NotNull val accountType: AccountType,
){
    fun toGrpcRequest(): CreatePixKeyRequest {
        return CreatePixKeyRequest.newBuilder()
            .setClientId(clientId)
            .setKeyType(type)
            .setKey(key)
            .setAccountType(accountType)
            .build()
    }
}
