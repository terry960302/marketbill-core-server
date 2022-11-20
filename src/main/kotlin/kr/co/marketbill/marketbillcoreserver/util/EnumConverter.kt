package kr.co.marketbill.marketbillcoreserver.util

import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade

class EnumConverter {
    companion object {
        fun convertFlowerGradeToKor(grade: FlowerGrade): String {
            return when (grade) {
                FlowerGrade.UPPER -> "상"
                FlowerGrade.MIDDLE -> "중"
                FlowerGrade.LOWER -> "하"
            }
        }
    }
}