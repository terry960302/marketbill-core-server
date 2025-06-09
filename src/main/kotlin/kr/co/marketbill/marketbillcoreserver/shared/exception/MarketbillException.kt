package kr.co.marketbill.marketbillcoreserver.shared.exception

import kr.co.marketbill.marketbillcoreserver.shared.constants.CustomErrorCode

class MarketbillException(val errorCode: CustomErrorCode) : RuntimeException(errorCode.message) {
    val status = errorCode.status
    val code = errorCode.code
}
