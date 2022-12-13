package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.OrderItemLoader
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.OrderSheetReceiptLoader
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import java.util.concurrent.CompletableFuture

@DgsComponent
class OrderFieldFetcher {
    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.OrderItems)
    fun orderItems(
        dfe: DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?
    ): CompletableFuture<List<OrderItem>> {
        val orderSheet = dfe.getSource<OrderSheet>()
        orderSheet.mapOrderItemRelatedFields()
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
        orderSheet.mapReceiptRelatedFields()
        val dataLoader = dfe.getDataLoader<Long, List<OrderSheetReceipt>>(OrderSheetReceiptLoader::class.java)

        val context = DgsContext.Companion.getCustomContext<CustomContext>(dfe)
        context.orderItemsInput.pagination = pagination

        return dataLoader.load(orderSheet.id)
    }
}