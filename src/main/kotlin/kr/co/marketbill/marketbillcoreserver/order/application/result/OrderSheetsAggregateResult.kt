package kr.co.marketbill.marketbillcoreserver.order.application.result

import java.time.LocalDate

data class OrderSheetsAggregateResult(
        val date: LocalDate?,
        val flowerTypesCount: Int,
        val orderSheetsCount: Int
) {
    companion object {
        fun from(
                date: LocalDate?,
                flowerTypesCount: Int,
                orderSheetsCount: Int
        ): OrderSheetsAggregateResult {
            return OrderSheetsAggregateResult(
                    date = date,
                    flowerTypesCount = flowerTypesCount,
                    orderSheetsCount = orderSheetsCount
            )
        }
    }
}
