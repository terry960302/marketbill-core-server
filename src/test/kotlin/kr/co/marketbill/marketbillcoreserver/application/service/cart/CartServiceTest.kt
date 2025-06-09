package kr.co.marketbill.marketbillcoreserver.application.service.cart

import java.util.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.FlowerType
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.validator.CartItemValidator
import kr.co.marketbill.marketbillcoreserver.domain.validator.ShoppingSessionValidator
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.CartItemRepository
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.ShoppingSessionRepository
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user.UserRepository
import kr.co.marketbill.marketbillcoreserver.shared.constants.FlowerGrade
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification

@ExtendWith(MockitoExtension::class)
class CartServiceTest {

    @Mock private lateinit var cartItemRepository: CartItemRepository

    @Mock private lateinit var shoppingSessionRepository: ShoppingSessionRepository

    @Mock private lateinit var userRepository: UserRepository

    @Mock private lateinit var cartItemValidator: CartItemValidator

    @Mock private lateinit var shoppingSessionValidator: ShoppingSessionValidator

    @InjectMocks private lateinit var cartService: CartService

    private lateinit var retailer: User
    private lateinit var wholesaler: User
    private lateinit var shoppingSession: ShoppingSession
    private lateinit var cartItem: CartItem
    private lateinit var flower: Flower
    private lateinit var flowerType: FlowerType

    @BeforeEach
    fun setUp() {
        retailer = User(name = "retailer", belongsTo = null)
        wholesaler = User(name = "wholesaler", belongsTo = "양재")
        flowerType = FlowerType(name = "장미")
        flower = Flower(name = "장미", flowerType = flowerType)
        shoppingSession =
                ShoppingSession.createWith(retailer = retailer, wholesaler = null, memo = null)
        cartItem =
                CartItem.createWith(
                        shoppingSession = shoppingSession,
                        retailer = retailer,
                        wholesaler = null,
                        flower = flower,
                        quantity = 1,
                        grade = FlowerGrade.LOWER
                )
    }

    @Test
    fun `장바구니 아이템 추가 성공`() {
        // given
        `when`(shoppingSessionRepository.findAllByRetailerId(retailer.id!!, any()))
                .thenReturn(PageImpl(listOf(shoppingSession)))
        `when`(cartItemRepository.save(any())).thenReturn(cartItem)

        // when
        cartService.addCartItem(retailer.id!!, flower.id!!, 1, FlowerGrade.LOWER)

        // then
        verify(cartItemValidator).validateCartItem(any())
        verify(cartItemRepository).save(any())
    }

    @Test
    fun `장바구니 아이템 수정 성공`() {
        // given
        `when`(cartItemRepository.findById(cartItem.id!!)).thenReturn(Optional.of(cartItem))
        `when`(cartItemRepository.save(any())).thenReturn(cartItem)

        // when
        cartService.updateCartItem(cartItem.id!!, 2, FlowerGrade.UPPER)

        // then
        verify(cartItemValidator).validateCartItem(any())
        verify(cartItemRepository).save(any())
    }

    @Test
    fun `장바구니 아이템 삭제 성공`() {
        // given
        `when`(cartItemRepository.findById(cartItem.id!!)).thenReturn(Optional.of(cartItem))

        // when
        cartService.removeCartItem(cartItem.id!!)

        // then
        verify(cartItemValidator).validateCartItem(any())
        verify(cartItemRepository).delete(cartItem)
    }

    @Test
    fun `쇼핑 세션 업데이트 성공`() {
        // given
        `when`(shoppingSessionRepository.findAllByRetailerId(retailer.id!!, any()))
                .thenReturn(PageImpl(listOf(shoppingSession)))
        `when`(shoppingSessionRepository.save(any())).thenReturn(shoppingSession)

        // when
        cartService.updateShoppingSession(retailer.id!!, wholesaler.id!!, "memo")

        // then
        verify(shoppingSessionValidator).validateShoppingSession(any())
        verify(shoppingSessionRepository).save(any())
    }

    @Test
    fun `장바구니 아이템 조회 성공`() {
        // given
        val pageRequest = PageRequest.of(0, 10)
        val cartItems = listOf(cartItem)
        `when`(cartItemRepository.findAll(any<Specification<CartItem>>(), eq(pageRequest)))
                .thenReturn(PageImpl(cartItems))

        // when
        val result =
                cartService.getAllPaginatedCartItemsByShoppingSessionIds(
                        listOf(shoppingSession.id!!),
                        pageRequest
                )

        // then
        assert(result.size == 1)
        assert(result[shoppingSession.id!!]?.content?.get(0) == cartItem)
    }
}
