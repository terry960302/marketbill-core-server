package kr.co.marketbill.marketbillcoreserver.legacy.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CustomOrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class CustomOrderItemSpecs {
    companion object {
        fun byOrderSheetIds(orderSheetIds: List<Long>): Specification<CustomOrderItem> {
            return Specification<CustomOrderItem> { root, query, builder ->
                if (orderSheetIds.isEmpty()) {
                    builder.conjunction()
                } else {
                    val orderSheet = root.join<CustomOrderItem, OrderSheet>("orderSheet")
                    orderSheet.get<Long>("id").`in`(orderSheetIds)
                }
            }
        }

        fun byOrderSheetId(orderSheetId: Long?): Specification<CustomOrderItem> {
            return Specification<CustomOrderItem> { root, query, builder ->
                if (orderSheetId == null) {
                    builder.conjunction()
                } else {
                    val orderSheet = root.join<CustomOrderItem, OrderSheet>("orderSheet")
                    builder.equal(orderSheet.get<Long>("id"), orderSheetId)
                }
            }
        }

        fun byFlowerName(flowerName: String?): Specification<CustomOrderItem> {
            return Specification<CustomOrderItem> { root, query, builder ->
                if (flowerName == null) {
                    builder.conjunction()
                } else {
                    builder.equal(root.get<String>("flowerName"), flowerName)
                }
            }
        }

        fun byFlowerTypeName(flowerTypeName: String?): Specification<CustomOrderItem> {
            return Specification<CustomOrderItem> { root, query, builder ->
                if (flowerTypeName == null) {
                    builder.conjunction()
                } else {
                    builder.equal(root.get<String>("flowerTypeName"), flowerTypeName)
                }
            }
        }


        fun byFlowerGrade(gradeKor: String?): Specification<CustomOrderItem> {
            return Specification<CustomOrderItem> { root, query, builder ->
                if (gradeKor == null) {
                    builder.conjunction()
                } else {
                    builder.equal(root.get<String>("grade"), gradeKor)
                }
            }
        }



    }
}