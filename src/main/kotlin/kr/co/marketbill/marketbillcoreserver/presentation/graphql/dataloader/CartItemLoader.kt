package kr.co.marketbill.marketbillcoreserver.presentation.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.presentation.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.application.service.cart.CartService
import kr.co.marketbill.marketbillcoreserver.shared.util.GqlDtoConverter
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


@DgsDataLoader(name = "cart_items")
class CartItemLoader : MappedBatchLoaderWithContext<Long, Page<CartItem>> {
    @Autowired
    private lateinit var cartService: CartService

    override fun load(
        keys: MutableSet<Long>?,
        env: BatchLoaderEnvironment
    ): CompletionStage<Map<Long, Page<CartItem>>> {
        val cartContext =
            DgsContext.getCustomContext<CustomContext>(env)
        val pagination = cartContext.cartItemsInput.pagination
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        val shoppingSessionIds = keys!!.stream().toList()
        val mappedCartItems = cartService.getAllPaginatedCartItemsByShoppingSessionIds(shoppingSessionIds, pageable)

        return CompletableFuture.supplyAsync { mappedCartItems }
    }
}