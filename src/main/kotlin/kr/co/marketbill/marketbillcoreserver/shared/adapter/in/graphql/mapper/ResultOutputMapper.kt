package kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.mapper

abstract class ResultOutputMapper<Result, Output> {
    abstract fun toOutput(result : Result): Output
}