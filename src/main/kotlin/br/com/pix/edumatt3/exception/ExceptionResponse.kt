package br.com.pix.edumatt3.exception

import io.micronaut.http.HttpStatus
import java.time.LocalDateTime
import java.time.ZoneId

data class ExceptionResponse(
    val status: HttpStatus,
    val message: String?,
    val path: String,
    val timestamp: Long = System.currentTimeMillis(),
)