package kr.co.marketbill.marketbillcoreserver.legacy.shared.exception

data class NotFoundException(override val message: String?) : RuntimeException()
