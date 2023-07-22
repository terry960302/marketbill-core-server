package kr.co.marketbill.marketbillcoreserver.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.service.CartService
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


@DgsDataLoader(name = "cart_items")
class CartItemLoader : MappedBatchLoaderWithContext<Long, List<CartItem>> {
    @Autowired
    private lateinit var cartService: CartService

    override fun load(
        keys: MutableSet<Long>?,
        env: BatchLoaderEnvironment
    ): CompletionStage<MutableMap<Long, List<CartItem>>> {
        val cartContext =
            DgsContext.getCustomContext<CustomContext>(env)
        val pagination = cartContext.cartItemsInput.pagination
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        val shoppingSessionIds = keys!!.stream().toList()
        val mappedCartItems: MutableMap<Long, List<CartItem>> = cartService.getAllCartItemsByShoppingSessionIds(
            shoppingSessionIds,
            pageable
        )

        return CompletableFuture.supplyAsync { mappedCartItems }
    }
}