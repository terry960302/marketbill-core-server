package kr.co.marketbill.marketbillcoreserver.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.graphql.context.OrderContext
import kr.co.marketbill.marketbillcoreserver.service.OrderService
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
            DgsContext.Companion.getCustomContext<OrderContext>(env)
        val input = orderContext.pagination
        var pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        val sort =
            if (input?.sort == kr.co.marketbill.marketbillcoreserver.types.Sort.DESCEND) {
                Sort.by("created_at").descending()
            } else {
                Sort.by("created_at").ascending()
            }
        if (input != null) {
            pageable = PageRequest.of(input.page!!, input.size!!, sort)
        }
        return CompletableFuture.supplyAsync {
            orderService.getAllOrderItemsByOrderSheetIds(
                keys!!.stream().toList(),
                pageable
            )
        }
    }
}