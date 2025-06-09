package kr.co.marketbill.marketbillcoreserver.shared.exception

import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.shared.constants.ErrorCode

class CustomException(
        override val message: String,
        val errorType: ErrorType,
        val errorCode: ErrorCode
) : RuntimeException() {}
