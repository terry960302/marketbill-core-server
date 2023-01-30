package kr.co.marketbill.marketbillcoreserver.graphql.error

import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.constants.CustomErrorCode

class CustomException(override val message: String, val errorType: ErrorType, val errorCode: CustomErrorCode) :
    RuntimeException() {
}