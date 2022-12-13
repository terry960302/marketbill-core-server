package kr.co.marketbill.marketbillcoreserver.graphql.error

data class NotFoundException(override val message: String?) : RuntimeException()
