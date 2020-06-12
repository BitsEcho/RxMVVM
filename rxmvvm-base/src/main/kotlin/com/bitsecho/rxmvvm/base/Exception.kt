package com.bitsecho.rxmvvm.base

import org.json.JSONException
import java.lang.RuntimeException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

enum class StatusCode(val code: Int, val message: String) {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    UNKNOWN_ERROR(500, "Unknown Error")
}

class AppException(val code: Int, override val message: String): RuntimeException()

fun StatusCode.toAppException(): AppException {
    return AppException(this.code, this.message)
}

open class AppExceptionConverter {
    open fun convert(e: Throwable): AppException {
        return when (e) {
            is SocketTimeoutException -> StatusCode.REQUEST_TIMEOUT.toAppException()
            is ConnectException,
            is UnknownHostException -> StatusCode.NOT_FOUND.toAppException()
            is JSONException,
            is ParseException -> StatusCode.UNPROCESSABLE_ENTITY.toAppException()
            is IllegalArgumentException -> StatusCode.BAD_REQUEST.toAppException()
            is AppException -> e
            else -> StatusCode.UNKNOWN_ERROR.toAppException()
        }
    }
}