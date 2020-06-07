package com.bitsecho.rxmvvm.base

import org.json.JSONException
import java.lang.RuntimeException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

object StatusCode {
    const val SUCCESS = 200
    const val BAD_REQUEST = 400
    const val NOT_FOUND = 404
    const val REQUEST_TIMEOUT = 408
    const val UNPROCESSABLE_ENTITY = 422
    const val UNKNOWN_ERROR = 520
}

class AppException(val code: Int, override val message: String): RuntimeException()

open class AppExceptionConverter {
    open fun convert(e: Throwable): AppException {
        return when (e) {
            is SocketTimeoutException -> AppException(StatusCode.REQUEST_TIMEOUT, "Request Timeout")
            is ConnectException,
            is UnknownHostException -> AppException(StatusCode.NOT_FOUND, "Not Found")
            is JSONException,
            is ParseException -> AppException(StatusCode.UNPROCESSABLE_ENTITY, "Unprocessable Entity")
            is IllegalArgumentException -> AppException(StatusCode.BAD_REQUEST, "Bad Request")
            is AppException -> e
            else -> AppException(StatusCode.UNKNOWN_ERROR, "Unknown Error")
        }
    }
}