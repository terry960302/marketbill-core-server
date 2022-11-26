package kr.co.marketbill.marketbillcoreserver.domain.dto

import java.time.LocalDate
import java.time.LocalDateTime

interface OrderStatisticOutput {
    fun getDate() : LocalDateTime
    fun getFlowerTypeCount() : Int
    fun getOrderSheetCount() : Int
}