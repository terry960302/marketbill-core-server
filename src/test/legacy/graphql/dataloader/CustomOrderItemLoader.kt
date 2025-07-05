package kr.co.marketbill.marketbillcoreserver.legacy.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CustomOrderItem
import kr.co.marketbill.marketbillcoreserver.presentation.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.application.service.order.OrderService
import kr.co.marketbill.marketbillcoreserver.shared.util.GqlDtoConverter
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


@DgsDataLoader(name = "custom_order_items")
class CustomOrderItemLoader : MappedBatchLoaderWithContext<Long, List<CustomOrderItem>> {
    @Autowired
    private lateinit var orderService: OrderService

    override fun load(
        keys: MutableSet<Long>?,
        env: BatchLoaderEnvironment
    ): CompletionStage<MutableMap<Long, List<CustomOrderItem>>> {
        val orderContext =
            DgsContext.getCustomContext<CustomContext>(env)
        val pagination = orderContext.customOrderItemsInput.pagination
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)


        val orderSheetIds = keys!!.stream().toList()
        val mappedCustomOrderItems: MutableMap<Long, List<CustomOrderItem>> = orderService.getAllCustomOrderItemsByOrderSheetIds(
            orderSheetIds,
            pageable
        )

        return CompletableFuture.supplyAsync { mappedCustomOrderItems }
    }
}