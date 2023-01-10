package kr.co.marketbill.marketbillcoreserver.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.service.OrderService
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


@DgsDataLoader(name = "order_items")
class OrderItemLoader : MappedBatchLoaderWithContext<Long, List<OrderItem>> {
    @Autowired
    private lateinit var orderService: OrderService

    override fun load(
        keys: MutableSet<Long>?,
        env: BatchLoaderEnvironment
    ): CompletionStage<MutableMap<Long, List<OrderItem>>> {
        val orderContext =
            DgsContext.getCustomContext<CustomContext>(env)
        val pagination = orderContext.orderItemsInput.pagination
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)


        val orderSheetIds = keys!!.stream().toList()
        val mappedOrderItems: MutableMap<Long, List<OrderItem>> = orderService.getAllOrderItemsByOrderSheetIds(
            orderSheetIds,
            pageable
        )

        return CompletableFuture.supplyAsync { mappedOrderItems }
    }
}