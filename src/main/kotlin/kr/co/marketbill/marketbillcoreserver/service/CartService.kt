package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.data.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.data.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.data.entity.user.User
import kr.co.marketbill.marketbillcoreserver.data.repository.order.CartRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class CartService {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var cartRepository: CartRepository

    val logger: Logger = LoggerFactory.getLogger(CartService::class.java)


    fun getAllCartItems(userId: Long, pageable: Pageable): Page<CartItem> {
        return cartRepository.getAllCartItems(userId, pageable)
    }

    fun addToCart(userId: Long, flowerId: Long, quantity: Int, grade: FlowerGrade): CartItem {
        val cartItem = CartItem(
            retailer = entityManager.getReference(User::class.java, userId),
            flower = entityManager.getReference(Flower::class.java, flowerId),
            quantity = quantity,
            grade = grade.toString()
        )
        return cartRepository.save(cartItem)
    }

    fun removeCartItem(cartItemId: Long): Long {
        try {
            cartRepository.deleteById(cartItemId)
            return cartItemId
        } catch (e: Exception) {
            logger.error(e.message)
            throw e
        }
    }


}