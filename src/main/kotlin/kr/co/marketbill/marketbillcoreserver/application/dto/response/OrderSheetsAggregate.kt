package kr.co.marketbill.marketbillcoreserver.application.dto.response

import java.time.LocalDateTime

interface OrderSheetsAggregate {
    fun getDate() : LocalDateTime?
    fun getFlowerTypesCount() : Int
    fun getOrderSheetsCount() : Int
}