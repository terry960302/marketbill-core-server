package kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.context.CartItemContext
import java.util.concurrent.CompletableFuture
import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.dataloader.CartItemsDataLoader
import kr.co.marketbill.marketbillcoreserver.types.CartItemsOutput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.types.ShoppingSession

@DgsComponent
class CartFieldFetcher {

    @DgsData(parentType = DgsConstants.SHOPPINGSESSION.TYPE_NAME, field = DgsConstants.SHOPPINGSESSION.CartItems)
    fun cartItems(
        dfe: com.netflix.graphql.dgs.DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?
    ): CompletableFuture<CartItemsOutput> {
        val shoppingSession = dfe.getSource<ShoppingSession>()
        val dataLoader = dfe.getDataLoader<Long, CartItemsOutput>(CartItemsDataLoader::class.java)
        val context = CartItemContext.from(pagination)
        return dataLoader.load(shoppingSession.id.toLong(), context)
    }
}
