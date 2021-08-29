package br.com.pix.edumatt3.pix.consult

import br.com.edumatt3.AccountType
import br.com.edumatt3.ConsultPixKeyResponse
import br.com.edumatt3.KeyType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class PixKeyDetailsResponse(
    grpcResponse: ConsultPixKeyResponse,
) {
    val clientId: String = grpcResponse.clientId
    val pixId: String = grpcResponse.pixId
    val keyType: KeyType = grpcResponse.pixKey.type

    val key: String = grpcResponse.pixKey.key
    val ownerName: String = grpcResponse.pixKey.account.customerName
    val ownerCpf: String = grpcResponse.pixKey.account.customerCpf
    val account = AccountDetailsResponse(grpcResponse.pixKey.account)
    val createdAt: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(grpcResponse.pixKey.createdAt.seconds, grpcResponse.pixKey.createdAt.nanos.toLong()), ZoneOffset.UTC)

    class AccountDetailsResponse(
        grpcAccountResponse: ConsultPixKeyResponse.PixKey.AccountInfo
    ){
        val name: String = grpcAccountResponse.instituition
        val branch: String = grpcAccountResponse.agency
        val number: String = grpcAccountResponse.accountNumber
        val accountType: AccountType = grpcAccountResponse.type
    }
}
