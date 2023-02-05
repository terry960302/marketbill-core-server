package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.*
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.CartItemLoader
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.CustomOrderItemLoader
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.OrderItemLoader
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.OrderSheetReceiptLoader
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter
import java.util.Optional
import java.util.concurrent.CompletableFuture

@DgsComponent
class OrderFieldFetcher {

    @DgsData(parentType = DgsConstants.SHOPPINGSESSION.TYPE_NAME, field = DgsConstants.SHOPPINGSESSION.CartItems)
    fun cartItems(
        dfe: DgsDataFetchingEnvironment, @InputArgument pagination: PaginationInput?
    ): CompletableFuture<List<CartItem>> {
        val shoppingSession = dfe.getSource<ShoppingSession>()
        val dataLoader = dfe.getDataLoader<Long, List<CartItem>>(CartItemLoader::class.java)

        val context = DgsContext.Companion.getCustomContext<CustomContext>(dfe)
        context.cartItemsInput.pagination = pagination

        return dataLoader.load(shoppingSession.id)
    }

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.TotalFlowerQuantity)
    fun totalFlowerQuantity(dfe: DgsDataFetchingEnvironment): Int {
        val orderSheet = dfe.getSource<OrderSheet>()

        val orderItemCount = if (orderSheet.orderItems.isNotEmpty()) {
            val quantities: List<Int> = orderSheet.orderItems.map { if (it.quantity == null) 0 else it.quantity!! }
            quantities.reduce { acc, i -> acc + i }
        } else {
            0
        }
        val customOrderItemCount = if (orderSheet.customOrderItems.isNotEmpty()) {
            val quantities: List<Int> =
                orderSheet.customOrderItems.map { if (it.quantity == null) 0 else it.quantity!! }
            quantities.reduce { acc, i -> acc + i }
        } else {
            0
        }
        return orderItemCount + customOrderItemCount
    }

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.TotalFlowerTypeCount)
    fun totalFlowerTypeCount(dfe: DgsDataFetchingEnvironment): Int {
        val orderSheet = dfe.getSource<OrderSheet>()
        val orderItemCount = if (orderSheet.orderItems.isNotEmpty()) {
            val flowerTypes: List<Long> = orderSheet.orderItems.mapNotNull { it.flower?.flowerType?.id }.distinct()
            flowerTypes.count()
        } else {
            0
        }
        val customOrderItemCount = if (orderSheet.customOrderItems.isNotEmpty()) {
            val flowerTypes: List<String> = orderSheet.customOrderItems.mapNotNull { it.flowerTypeName }.distinct()
            flowerTypes.count()
        } else {
            0
        }
        return orderItemCount + customOrderItemCount
    }

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.OrderItems)
    fun orderItems(
        dfe: DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?
    ): CompletableFuture<List<OrderItem>> {
        val orderSheet = dfe.getSource<OrderSheet>()
        val dataLoader = dfe.getDataLoader<Long, List<OrderItem>>(OrderItemLoader::class.java)

        val context = DgsContext.Companion.getCustomContext<CustomContext>(dfe)
        context.orderItemsInput.pagination = pagination

        return dataLoader.load(orderSheet.id)
    }

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.CustomOrderItems)
    fun customOrderItems(
        dfe: DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?
    ): CompletableFuture<List<CustomOrderItem>> {
        val orderSheet = dfe.getSource<OrderSheet>()
        val dataLoader = dfe.getDataLoader<Long, List<CustomOrderItem>>(CustomOrderItemLoader::class.java)

        val context = DgsContext.Companion.getCustomContext<CustomContext>(dfe)
        context.customOrderItemsInput.pagination = pagination

        return dataLoader.load(orderSheet.id)
    }

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.OrderSheetReceipts)
    fun orderSheetReceipts(
        dfe: DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?
    ): CompletableFuture<List<OrderSheetReceipt>> {
        val orderSheet = dfe.getSource<OrderSheet>()
        val dataLoader = dfe.getDataLoader<Long, List<OrderSheetReceipt>>(OrderSheetReceiptLoader::class.java)

        val context = DgsContext.Companion.getCustomContext<CustomContext>(dfe)
        context.orderItemsInput.pagination = pagination

        return dataLoader.load(orderSheet.id)
    }

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.HasReceipt)
    fun hasReceipt(dfe: DgsDataFetchingEnvironment): Boolean {
        val orderSheet = dfe.getSource<OrderSheet>()
        return orderSheet.orderSheetReceipts.isNotEmpty()
    }

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.RecentReceipt)
    fun recentReceipt(dfe: DgsDataFetchingEnvironment): Optional<OrderSheetReceipt> {
        val orderSheet = dfe.getSource<OrderSheet>()
        val orderSheetReceiptsSortByDesc: List<OrderSheetReceipt> =
            orderSheet.orderSheetReceipts.sortedByDescending { it.createdAt }
        return if (orderSheetReceiptsSortByDesc.isEmpty()) {
            Optional.empty<OrderSheetReceipt>()
        } else {
            Optional.of(orderSheetReceiptsSortByDesc[0])
        }
    }

    @DgsData(parentType = DgsConstants.CARTITEM.TYPE_NAME, field = DgsConstants.CARTITEM.Grade)
    fun cartItemGrade(dfe: DgsDataFetchingEnvironment): FlowerGrade {
        val cartItem = dfe.getSource<CartItem>()
        cartItem.gradeValue = EnumConverter.convertFlowerGradeKorToEnum(cartItem.grade!!)
        return cartItem.gradeValue!!
    }

    @DgsData(parentType = DgsConstants.ORDERITEM.TYPE_NAME, field = DgsConstants.ORDERITEM.Grade)
    fun orderItemGrade(dfe: DgsDataFetchingEnvironment): FlowerGrade {
        val orderItem = dfe.getSource<OrderItem>()
        orderItem.gradeValue = EnumConverter.convertFlowerGradeKorToEnum(orderItem.grade!!)
        return orderItem.gradeValue!!
    }

    @DgsData(parentType = DgsConstants.DAILYORDERITEM.TYPE_NAME, field = DgsConstants.DAILYORDERITEM.Grade)
    fun dailyOrderItemGrade(dfe: DgsDataFetchingEnvironment): FlowerGrade {
        val dailyOrderItem = dfe.getSource<DailyOrderItem>()
        dailyOrderItem.gradeValue = EnumConverter.convertFlowerGradeKorToEnum(dailyOrderItem.grade!!)
        return dailyOrderItem.gradeValue!!
    }
}