package kr.co.marketbill.marketbillcoreserver.cart.application.usecase

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.cart.application.command.UpdateShoppingSessionCommand
import kr.co.marketbill.marketbillcoreserver.cart.application.port.outbound.CartRepository
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import kr.co.marketbill.marketbillcoreserver.user.application.port.outbound.UserRepository
import org.springframework.stereotype.Component

@Component
class UpdateShoppingSessionUseCase(
        private val cartRepository: CartRepository,
        private val userRepository: UserRepository
) {
        fun execute(command: UpdateShoppingSessionCommand): ShoppingSession {
                val retailer =
                        userRepository.findById(command.retailerId)
                                ?: throw MarketbillException(ErrorCode.NO_RETAILER)

                val wholesaler =
                        command.wholesalerId?.let { wholesalerId ->
                                userRepository.findById(wholesalerId)
                                        ?: throw MarketbillException(ErrorCode.NO_WHOLESALER)
                        }

                // 기존 쇼핑 세션 조회 또는 생성
                val shoppingSession =
                        cartRepository.findShoppingSessionByRetailerId(command.retailerId)
                                ?: ShoppingSession(
                                        retailer = retailer,
                                        createdAt = LocalDateTime.now(),
                                        updatedAt = LocalDateTime.now()
                                )

                val updatedShoppingSession =
                        shoppingSession.updateWholesaler(wholesaler!!).updateMemo(command.memo)

                return cartRepository.saveShoppingSession(updatedShoppingSession)
        }
}
