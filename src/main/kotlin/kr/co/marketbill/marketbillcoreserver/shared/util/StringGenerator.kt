package kr.co.marketbill.marketbillcoreserver.shared.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class StringGenerator {
    companion object{
        fun generateOrderNo(orderSheetId: Long, digit: Int = 5): String {
            val symbolChar = "M"
            val formatter = SimpleDateFormat("yyMMdd")
            val dateStr = formatter.format(Date())

            val id: Long = if (digit <= orderSheetId.toString().length) {
                (orderSheetId % (10.toDouble().pow(digit))).toLong()
            } else {
                orderSheetId
            }

            var uniqueNum = id.toString()
            for (i in (1..(digit - id.toString().length))) {
                uniqueNum = "0$uniqueNum"
            }

            return "$dateStr$symbolChar$uniqueNum"
        }
    }
}