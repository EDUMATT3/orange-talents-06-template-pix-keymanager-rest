package br.com.pix.edumatt3.exception

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.hateoas.Link
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class StatusRuntimeExceptionHandler: ExceptionHandler<StatusRuntimeException, HttpResponse<JsonError>> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handle(request: HttpRequest<*>, e: StatusRuntimeException): HttpResponse<JsonError> {

        val status = when(e.status.code){
            Status.INVALID_ARGUMENT.code -> HttpStatus.BAD_REQUEST
            Status.ALREADY_EXISTS.code -> HttpStatus.UNPROCESSABLE_ENTITY
            Status.NOT_FOUND.code -> HttpStatus.NOT_FOUND
            Status.PERMISSION_DENIED.code -> HttpStatus.FORBIDDEN
            Status.INTERNAL.code -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.BAD_GATEWAY
        }

        logger.warn("A exception was ocurred with status: [$status] and description: [${e.status.description}]")

        val jsonError = JsonError(e.status.description ?: "an error has occurred").link(Link.SELF, Link.of(request.uri))

        return HttpResponse.status<JsonError>(status).body(jsonError)
    }

}