package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.OrderItemLoader
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.OrderSheetReceiptLoader
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import java.util.Optional
import java.util.concurrent.CompletableFuture

@DgsComponent
class OrderFieldFetcher {

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.TotalFlowerQuantity)
    fun totalFlowerQuantity(dfe: DgsDataFetchingEnvironment): Int {
        val orderSheet = dfe.getSource<OrderSheet>()
        return if (orderSheet.orderItems.isNotEmpty()) {
            val quantities: List<Int> = orderSheet.orderItems.map { if (it.quantity == null) 0 else it.quantity!! }
            quantities.reduce { acc, i -> acc + i }
        } else {
            0
        }
    }

    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.TotalFlowerTypeCount)
    fun totalFlowerTypeCount(dfe: DgsDataFetchingEnvironment): Int {
        val orderSheet = dfe.getSource<OrderSheet>()
        return if (orderSheet.orderItems.isNotEmpty()) {
            val flowerTypes: List<Long> = orderSheet.orderItems.mapNotNull { it.flower?.flowerType?.id }.distinct()
            flowerTypes.count()
        } else {
            0
        }
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
        val orderSheetReceiptsSortByDesc: List<OrderSheetReceipt>? =
            orderSheet.orderSheetReceipts.sortedByDescending { it.createdAt }
        return if (orderSheetReceiptsSortByDesc == null || orderSheetReceiptsSortByDesc.isEmpty()) {
            Optional.empty<OrderSheetReceipt>()
        } else {
            Optional.of(orderSheetReceiptsSortByDesc[0])
        }
    }

    @DgsData(parentType = DgsConstants.CARTITEM.TYPE_NAME, field = DgsConstants.CARTITEM.Grade)
    fun cartItemGrade(dfe: DgsDataFetchingEnvironment): FlowerGrade {
        val cartItem = dfe.getSource<CartItem>()
        return cartItem.gradeValue!!
    }

    @DgsData(parentType = DgsConstants.ORDERITEM.TYPE_NAME, field = DgsConstants.ORDERITEM.Grade)
    fun orderItemGrade(dfe: DgsDataFetchingEnvironment): FlowerGrade {
        val orderItem = dfe.getSource<OrderItem>()
        return orderItem.gradeValue!!
    }
}