package kr.co.marketbill.marketbillcoreserver.cart.application.usecase

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.cart.application.command.AddToCartCommand
import kr.co.marketbill.marketbillcoreserver.cart.application.port.outbound.CartRepository
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.CartItem
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.flower.application.port.outbound.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import kr.co.marketbill.marketbillcoreserver.user.application.port.outbound.UserRepository
import org.springframework.stereotype.Component

@Component
class AddToCartUseCase(
        private val cartRepository: CartRepository,
        private val userRepository: UserRepository,
        private val flowerRepository: FlowerRepository
) {
        fun execute(command: AddToCartCommand): CartItem {
                val retailer =
                        userRepository.findById(command.retailerId)
                                ?: throw MarketbillException(ErrorCode.NO_USER)

                val flower =
                        flowerRepository.findById(command.flowerId)
                                ?: throw MarketbillException(ErrorCode.NO_FLOWER)

                // 기존 쇼핑 세션 조회 또는 생성
                val shoppingSession =
                        cartRepository.findShoppingSessionByRetailerId(command.retailerId)
                                ?: ShoppingSession(
                                        retailer = retailer,
                                        createdAt = LocalDateTime.now(),
                                        updatedAt = LocalDateTime.now()
                                )

                // 새로운 CartItem 생성
                val cartItem =
                        CartItem(
                                retailer = retailer,
                                flower = flower,
                                quantity = command.quantity,
                                grade = command.grade,
                                memo = command.memo,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now()
                        )

                // CartItem 저장
                val savedCartItem = cartRepository.saveCartItem(cartItem)

                // ShoppingSession 업데이트
                val updatedShoppingSession = shoppingSession.addCartItem(savedCartItem)
                cartRepository.saveShoppingSession(updatedShoppingSession)

                return savedCartItem
        }
}
