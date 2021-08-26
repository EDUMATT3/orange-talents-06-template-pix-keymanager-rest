package br.com.pix.edumatt3

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.hateoas.Link
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Produces
@Singleton
class StatusRuntimeExceptionHandler: ExceptionHandler<StatusRuntimeException, HttpResponse<JsonError>> {
    override fun handle(request: HttpRequest<*>, e: StatusRuntimeException): HttpResponse<JsonError> {

        var status = HttpStatus.INTERNAL_SERVER_ERROR

        status = when(e.status.code){
            Status.INVALID_ARGUMENT.code -> HttpStatus.BAD_REQUEST
            Status.ALREADY_EXISTS.code -> HttpStatus.UNPROCESSABLE_ENTITY
            Status.NOT_FOUND.code -> HttpStatus.NOT_FOUND
            Status.PERMISSION_DENIED.code -> HttpStatus.FORBIDDEN
            Status.INTERNAL.code -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.BAD_GATEWAY
        }

        val jsonError = JsonError(e.status.description ?: "an error has occurred").link(Link.SELF, Link.of(request.uri))

        return HttpResponse.status<JsonError>(status).body(jsonError)
    }

}