package kr.co.marketbill.marketbillcoreserver.legacy.shared.util

import kr.co.marketbill.marketbillcoreserver.shared.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.shared.exception.InternalErrorException

class EnumConverter {
    companion object {
        fun convertFlowerGradeToKor(grade: FlowerGrade): String {
            return when (grade) {
                FlowerGrade.UPPER -> "상"
                FlowerGrade.MIDDLE -> "중"
                FlowerGrade.LOWER -> "하"
            }
        }

        fun convertFlowerGradeKorToEnum(grade: String): FlowerGrade {
            return when (grade) {
                "상" -> FlowerGrade.UPPER
                "중" -> FlowerGrade.MIDDLE
                "하" -> FlowerGrade.LOWER
                else -> throw InternalErrorException(message = "Invalid FlowerGrade String value to convert.")
            }
        }
    }
}