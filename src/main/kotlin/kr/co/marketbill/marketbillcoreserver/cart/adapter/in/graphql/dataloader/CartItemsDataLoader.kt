package kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.context.CartItemContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.mapper.CartOutputMapper
import kr.co.marketbill.marketbillcoreserver.cart.application.command.FindShoppingSessionCommand
import kr.co.marketbill.marketbillcoreserver.cart.application.service.CartService
import kr.co.marketbill.marketbillcoreserver.types.CartItemsOutput
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.stereotype.Component

@Component
@DgsDataLoader(name = "cartItems")
class CartItemsDataLoader(
        private val cartService: CartService,
        private val cartOutputMapper: CartOutputMapper
) : MappedBatchLoaderWithContext<Long, CartItemsOutput> {

    override fun load(
            keys: Set<Long>,
            environment: org.dataloader.BatchLoaderEnvironment
    ): CompletionStage<Map<Long, CartItemsOutput>> {
        val context = environment.keyContexts as CartItemContext
        val command = FindShoppingSessionCommand.from(keys)
        val results = cartService.findShoppingSessionsByRetailerIds(command)

        val cartItemsMap =
                results.mapValues { (_, shoppingSession) ->
                    CartItemsOutput(
                            resultCount = shoppingSession.cartItems.size,
                            items =
                                    shoppingSession.cartItems.map {
                                        cartOutputMapper.toCartItemOutput(it)
                                    }
                    )
                }

        return CompletableFuture.completedFuture(cartItemsMap.mapKeys { it.key.value })
    }
}
