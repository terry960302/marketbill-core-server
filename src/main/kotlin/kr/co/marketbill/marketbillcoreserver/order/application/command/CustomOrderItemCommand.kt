package kr.co.marketbill.marketbillcoreserver.order.application.command

import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.types.CustomOrderItemInput

data class CustomOrderItemCommand(
    val id: Long?,
    val flowerName: String?,
    val flowerTypeName: String?,
    val grade: FlowerGrade?,
    val quantity: Int?,
    val price: Int?
) {
    companion object {
        fun from(input: CustomOrderItemInput): CustomOrderItemCommand {
            return CustomOrderItemCommand(
                id = input.id?.toLong(),
                flowerName = input.flowerName,
                flowerTypeName = input.flowerTypeName,
                grade = input.grade?.let { FlowerGrade.valueOf(input.grade.name) },
                quantity = input.quantity,
                price = input.price,
            )
        }
    }
}