package kr.co.marketbill.marketbillcoreserver.legacy.application.service.cart

import javax.persistence.EntityManager
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.BatchCartToOrderLogs
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.validator.CartItemValidator
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.BatchCartToOrderLogsRepository
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order.CartItemRepository
import kr.co.marketbill.marketbillcoreserver.infrastructure.repository.user.UserRepository
import kr.co.marketbill.marketbillcoreserver.shared.constants.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.exception.MarketbillException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BatchProcessor(
    private val cartItemRepository: CartItemRepository,
    private val batchCartToOrderLogsRepository: BatchCartToOrderLogsRepository,
    private val entityManager: EntityManager,
    private val cartItemService: kr.co.marketbill.marketbillcoreserver.legacy.application.service.cart.CartItemService,
    private val shoppingSessionService: kr.co.marketbill.marketbillcoreserver.legacy.application.service.cart.ShoppingSessionService,
    private val orderConverter: kr.co.marketbill.marketbillcoreserver.legacy.application.service.cart.OrderConverter,
    private val cartItemValidator: CartItemValidator
) {

    /** 매일 오후 10시에 자동으로 장바구니 상품을 주문으로 변환합니다. */
    @Scheduled(cron = "0 0 22 * * ?", zone = "Asia/Seoul")
    @Transactional
    fun processBatchOrder() {
        val log =
                BatchCartToOrderLogs(
                        cartItemsCount = 0,
                        orderSheetCount = -1,
                        orderItemCount = -1,
                        errLogs = ""
                )

        try {
            val validCartItems = findValidCartItems()
            log.cartItemsCount = validCartItems.size

            val cartItemGroup = validCartItems.groupBy { it.retailer.id!! }
            processCartItemGroups(cartItemGroup)

            log.orderSheetCount = cartItemGroup.size
            log.orderItemCount = validCartItems.size
        } catch (e: Exception) {
            log.errLogs = e.message
            throw e
        } finally {
            batchCartToOrderLogsRepository.save(log)
        }
    }

    /** 유효한 장바구니 아이템들을 조회합니다. */
    private fun findValidCartItems(): List<CartItem> {
        return cartItemRepository.findAll().filter { it.wholesaler != null }.also { items ->
            items.forEach { cartItemValidator.validateCartItem(it) }
        }
    }

    /** 장바구니 아이템 그룹을 처리합니다. */
    private fun processCartItemGroups(cartItemGroup: Map<Long, List<CartItem>>) {
        cartItemGroup.forEach { (retailerId, cartItems) ->
            val retailer = entityManager.getReference(User::class.java, retailerId)
            val session = shoppingSessionService.findSessionByRetailerId(retailerId)
            val wholesaler = session.wholesaler!!

            // 장바구니 아이템을 주문 상태로 변경
            val orderedCartItems = cartItems.map { cartItemService.markAsOrdered(it) }
            cartItemRepository.saveAll(orderedCartItems)

            // 장바구니 세션 삭제
            if(session == null) throw MarketbillException(ErrorCode.NO_SHOPPING_SESSION)
            shoppingSessionService.deleteSession(session)

            // 주문 생성
            orderConverter.convertCartItemsToOrder(retailer, wholesaler, cartItems)
        }
    }
}
