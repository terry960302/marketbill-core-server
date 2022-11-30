package kr.co.marketbill.marketbillcoreserver.domain.dto

import java.time.LocalDateTime

interface OrderSheetsAggregate {
    fun getDate() : LocalDateTime
    fun getFlowerTypesCount() : Int
    fun getOrderSheetsCount() : Int
}