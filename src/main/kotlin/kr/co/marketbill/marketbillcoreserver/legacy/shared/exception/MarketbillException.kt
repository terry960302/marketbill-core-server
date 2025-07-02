package kr.co.marketbill.marketbillcoreserver.legacy.shared.exception

import kr.co.marketbill.marketbillcoreserver.shared.constants.ErrorCode

class MarketbillException(
    val errorCode: ErrorCode,
    val cause1: Exception? = null,
    val cause2: Exception? = null,
) : RuntimeException(errorCode.message, cause1) {
    val status = errorCode.status
    val code = errorCode.code
}
