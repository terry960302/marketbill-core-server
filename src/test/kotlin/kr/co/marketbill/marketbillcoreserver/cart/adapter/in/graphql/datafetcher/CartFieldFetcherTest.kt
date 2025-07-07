package kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.datafetcher

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.dataloader.CartItemsDataLoader
import kr.co.marketbill.marketbillcoreserver.types.CartItemsOutput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.types.ShoppingSession
import org.dataloader.DataLoader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

@ExtendWith(MockitoExtension::class)
class CartFieldFetcherTest {

    @Mock
    private lateinit var dfe: DgsDataFetchingEnvironment

    @Mock
    private lateinit var dataLoader: DataLoader<Long, CartItemsOutput>

    private lateinit var cartFieldFetcher: CartFieldFetcher

    @BeforeEach
    fun setUp() {
        cartFieldFetcher = CartFieldFetcher()
    }

    @Test
    fun `쇼핑 세션의 장바구니 아이템을 로드할 수 있다`() {
        // given
        val cartItemsOutput = CartItemsOutput(
            resultCount = 2,
            items = emptyList()
        )
        
        val shoppingSession = ShoppingSession(
            id = 1,
            retailer = null,
            wholesaler = null,
            memo = "테스트 세션",
            cartItems = cartItemsOutput,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            deletedAt = null
        )
        
        `when`(dfe.getSource<ShoppingSession>()).thenReturn(shoppingSession)
        `when`(dfe.getDataLoader<Long, CartItemsOutput>(CartItemsDataLoader::class.java)).thenReturn(dataLoader)
        `when`(dataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(cartItemsOutput))
        
        // when
        val result = cartFieldFetcher.cartItems(dfe, null)
        
        // then
        assertEquals(cartItemsOutput, result.get())
    }
} 