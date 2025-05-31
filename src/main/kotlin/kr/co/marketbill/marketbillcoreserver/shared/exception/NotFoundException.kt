package kr.co.marketbill.marketbillcoreserver.shared.exception

data class NotFoundException(override val message: String?) : RuntimeException()
