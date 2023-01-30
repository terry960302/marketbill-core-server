package kr.co.marketbill.marketbillcoreserver.graphql.error

class InternalErrorException(override var message : String = "") : RuntimeException() {

}