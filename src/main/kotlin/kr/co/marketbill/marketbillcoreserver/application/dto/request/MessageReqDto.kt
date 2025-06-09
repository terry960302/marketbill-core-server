package kr.co.marketbill.marketbillcoreserver.application.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.shared.constants.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.exception.CustomException

data class MessageReqDto(
    @JsonProperty("to")
    val to: String,
    @JsonProperty("template")
    val template: String,
    @JsonProperty("args")
    val args: List<*>,
)

    fun validate() {
        if (to.isBlank()) {
            throw CustomException(
                message = "Receiver number must not be blank.",
                errorType = ErrorType.BAD_REQUEST,
                errorCode = ErrorCode.INVALID_DATA,
            )
        }
        if (args.any { it == null || it.toString().isBlank() }) {
            throw CustomException(
                message = "Arguments must not be blank.",
                errorType = ErrorType.BAD_REQUEST,
                errorCode = ErrorCode.INVALID_DATA,
            )
        }
    }
