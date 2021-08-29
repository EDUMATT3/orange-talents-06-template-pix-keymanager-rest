package br.com.pix.edumatt3.pix.list

import br.com.edumatt3.ListPixKeyResponse
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected

class PixKeyResponse(
    pixKey: ListPixKeyResponse.PixKey
){
    val pixId = pixKey.pixId
    val keyType = pixKey.keyType
    val key = pixKey.key
    val createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(pixKey.createdAt.seconds, pixKey.createdAt.nanos.toLong()), ZoneOffset.UTC)
}
