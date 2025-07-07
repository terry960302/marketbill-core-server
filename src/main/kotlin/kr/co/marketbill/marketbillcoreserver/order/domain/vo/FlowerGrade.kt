package kr.co.marketbill.marketbillcoreserver.order.domain.vo

enum class FlowerGrade {
    UPPER,
    MIDDLE,
    LOWER;

    companion object {
        fun from(grade: kr.co.marketbill.marketbillcoreserver.types.FlowerGrade): FlowerGrade {
            return FlowerGrade.valueOf(grade.name)
        }
    }
}
