package kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response

import java.time.LocalDateTime

interface OrderSheetsAggregate {
    fun getDate() : LocalDateTime?
    fun getFlowerTypesCount() : Int
    fun getOrderSheetsCount() : Int
}