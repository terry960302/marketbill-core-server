package kr.co.marketbill.marketbillcoreserver.cart.application.usecase

import kr.co.marketbill.marketbillcoreserver.cart.application.command.FindShoppingSessionCommand
import kr.co.marketbill.marketbillcoreserver.cart.application.port.outbound.CartRepository
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.ShoppingSessionId
import org.springframework.stereotype.Component

@Component
class FindShoppingSessionUseCase(private val cartRepository: CartRepository) {
    fun execute(command: FindShoppingSessionCommand): ShoppingSession? {
        return cartRepository.findShoppingSessionByRetailerId(command.retailerId!!)
    }

    fun executeBatch(command: FindShoppingSessionCommand): Map<ShoppingSessionId, ShoppingSession> {
        return cartRepository.findShoppingSessionsByRetailerIds(command.retailerIds!!)
    }
}
