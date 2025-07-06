package kr.co.marketbill.marketbillcoreserver.order.adapter.`in`.graphql.mapper

import kr.co.marketbill.marketbillcoreserver.order.application.result.*
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerResult
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerColorResult
import kr.co.marketbill.marketbillcoreserver.types.*
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class OrderOutputMapper {

    fun toOrderSheetOutput(result: OrderSheetResult): OrderSheet {
        return OrderSheet(
            id = result.id.toInt(),
            orderNo = result.orderNo,
            retailer = result.retailer?.let { toUserOutput(it) },
            wholesaler = result.wholesaler?.let { toUserOutput(it) },
            orderItems = result.orderItems.map { toOrderItemOutput(it) },
            customOrderItems = result.customOrderItems.map { toCustomOrderItemOutput(it) },
            totalFlowerQuantity = result.totalFlowerQuantity,
            totalFlowerTypeCount = result.totalFlowerTypeCount,
            totalFlowerPrice = result.totalFlowerPrice,
            hasReceipt = result.hasReceipt,
            orderSheetReceipts = result.orderSheetReceipts.map { toOrderSheetReceiptOutput(it) },
            recentReceipt = result.recentReceipt?.let { toOrderSheetReceiptOutput(it) },
            isPriceUpdated = result.isPriceUpdated,
            memo = result.memo,
            createdAt = result.createdAt.toLocalDate(),
            updatedAt = result.updatedAt.toLocalDate(),
            deletedAt = result.deletedAt?.toLocalDate()
        )
    }

    fun toOrderItemOutput(result: OrderItemResult): OrderItem {
        return OrderItem(
            id = result.id.toInt(),
            orderSheet = toOrderSheetOutput(result.orderSheet),
            retailer = result.retailer?.let { toUserOutput(it) },
            wholesaler = result.wholesaler?.let { toUserOutput(it) },
            flower = toFlowerOutput(result.flower),
            quantity = result.quantity,
            grade = FlowerGrade.valueOf(result.grade),
            price = result.price,
            createdAt = result.createdAt.toLocalDate(),
            updatedAt = result.updatedAt.toLocalDate(),
            deletedAt = result.deletedAt?.toLocalDate(),
            memo = result.memo
        )
    }

    fun toCustomOrderItemOutput(result: CustomOrderItemResult): CustomOrderItem {
        return CustomOrderItem(
            id = result.id.toInt(),
            orderSheet = toOrderSheetOutput(result.orderSheet),
            retailer = result.retailer?.let { toUserOutput(it) },
            wholesaler = result.wholesaler?.let { toUserOutput(it) },
            flowerName = result.flowerName,
            flowerTypeName = result.flowerTypeName,
            quantity = result.quantity,
            grade = result.grade?.let { FlowerGrade.valueOf(it) },
            createdAt = result.createdAt.toLocalDate(),
            updatedAt = result.updatedAt.toLocalDate(),
            deletedAt = result.deletedAt?.toLocalDate()
        )
    }

    fun toDailyOrderItemOutput(result: DailyOrderItemResult): DailyOrderItem {
        return DailyOrderItem(
            id = result.id.toInt(),
            wholesaler = result.wholesaler?.let { toUserOutput(it) },
            flower = toFlowerOutput(result.flower),
            grade = FlowerGrade.valueOf(result.grade),
            price = result.price,
            createdAt = result.createdAt.toLocalDate(),
            updatedAt = result.updatedAt.toLocalDate(),
            deletedAt = result.deletedAt?.toLocalDate()
        )
    }

    fun toOrderSheetReceiptOutput(result: OrderSheetReceiptResult): OrderSheetReceipt {
        return OrderSheetReceipt(
            id = result.id.toInt(),
            orderSheet = toOrderSheetOutput(result.orderSheet),
            fileName = result.fileName,
            filePath = result.filePath,
            fileFormat = result.fileFormat,
            metadata = result.metadata,
            createdAt = result.createdAt.toLocalDate(),
            updatedAt = result.updatedAt.toLocalDate(),
            deletedAt = result.deletedAt?.toLocalDate()
        )
    }

    fun toOrderSheetsAggregateOutput(result: OrderSheetsAggregateResult): OrderSheetsAggregate {
        return OrderSheetsAggregate(
            date = result.date,
            flowerTypesCount = result.flowerTypesCount,
            orderSheetsCount = result.orderSheetsCount
        )
    }

    private fun toUserOutput(result: UserResult): User {
        return User(
            id = result.id!!.value.toInt(),
            name = result.name.value,
            belongsTo = null,
            businessInfo = null,
            userCredential = UserCredential(
                id = 0,
                phoneNo = result.phoneNumber.value,
                role = AccountRole.valueOf(result.role.name),
                createdAt = LocalDate.from(result.createdAt)
            ),
            appliedConnections = emptyList(),
            receivedConnections = emptyList(),
            connectedEmployees = emptyList(),
            connectedEmployer = null,
            deletedAt = null
        )
    }

    private fun toFlowerOutput(result: FlowerResult): Flower {
        return Flower(
            id = result.id.toInt(),
            flowerType = FlowerType(
                id = 0,
                name = result.flowerTypeName,
                imgUrl = result.flowerTypeImgUrl,
            ),
            name = result.name,
            images = result.flowerImages,
            biddingFlowers = emptyList(),
            flowerColor = result.flowerColor?.let { toFlowerColorOutput(it) }
        )
    }

    private fun toFlowerColorOutput(result: FlowerColorResult): FlowerColor {
        return FlowerColor(
            id = result.id.toInt(),
            name = result.name,
        )
    }
} 