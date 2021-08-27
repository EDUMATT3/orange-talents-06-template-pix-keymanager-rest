package br.com.pix.edumatt3.exception

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StatusRuntimeExceptionHandlerTest{

    @Test
    internal fun `should return the correct http status`() {

        val pairs = mutableMapOf(HttpStatus.BAD_REQUEST to Status.INVALID_ARGUMENT,
            HttpStatus.INTERNAL_SERVER_ERROR to Status.INTERNAL,
        HttpStatus.UNPROCESSABLE_ENTITY to Status.ALREADY_EXISTS,
        HttpStatus.NOT_FOUND to Status.NOT_FOUND,
        HttpStatus.FORBIDDEN to Status.PERMISSION_DENIED)

        pairs.forEach{
            val exception = StatusRuntimeException(it.value)
            val request = HttpRequest.GET<Any>("/")

            val error = StatusRuntimeExceptionHandler().handle(request, exception)

            assertEquals(error.status, it.key)
        }
    }
}