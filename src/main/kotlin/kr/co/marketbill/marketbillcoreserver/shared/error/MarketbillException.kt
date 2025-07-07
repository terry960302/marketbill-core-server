package kr.co.marketbill.marketbillcoreserver.shared.error

import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode

class MarketbillException(
        val errorCode: ErrorCode
) : RuntimeException() {}
