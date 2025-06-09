package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import java.time.LocalDateTime
import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.shared.constants.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.shared.exception.MarketbillException

@Entity
@Table(name = "cart_items")
class CartItem
private constructor(
    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "shopping_session_id", nullable = false)
        val shoppingSession: ShoppingSession,
    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "retailer_id", nullable = false)
        val retailer: User,
    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "wholesaler_id")
        val wholesaler: User?,
    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "flower_id", nullable = false)
        val flower: Flower,
    @Column(nullable = false) val quantity: Int,
    @Column(nullable = false) val grade: String,
    @Column(name = "ordered_at") var orderedAt: LocalDateTime? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
) {
    companion object {
        fun createWith(
                shoppingSession: ShoppingSession,
                retailer: User,
                wholesaler: User?,
                flower: Flower,
                quantity: Int,
                grade: String
        ): CartItem {
            return CartItem(
                    shoppingSession = shoppingSession,
                    retailer = retailer,
                    wholesaler = wholesaler,
                    flower = flower,
                    quantity = quantity,
                    grade = grade
            )
        }
    }

    /** 동일한 상품인지 확인합니다. 꽃, 등급, 도매상이 모두 동일한 경우 true를 반환합니다. */
    fun isSameItem(other: CartItem): Boolean {
        return this.flower == other.flower &&
                this.grade == other.grade &&
                this.wholesaler?.id == other.wholesaler?.id
    }

    /** 다른 장바구니 아이템과 병합합니다. 동일한 상품인 경우에만 수량을 합산합니다. */
    fun mergeWith(other: CartItem): CartItem {
        if (!isSameItem(other)) {
            throw MarketbillException(ErrorCode.INVALID_DATA)
        }
        return copy(quantity = this.quantity + other.quantity)
    }

    /** 수량을 업데이트합니다. */
    fun updateQuantity(newQuantity: Int): CartItem {
        return copy(quantity = newQuantity)
    }

    /** 등급을 업데이트합니다. */
    fun updateGrade(newGrade: String): CartItem {
        return copy(grade = newGrade)
    }

    /** 도매상을 업데이트합니다. */
    fun updateWholesaler(newWholesaler: User?): CartItem {
        return copy(wholesaler = newWholesaler)
    }

    /** 주문 상태로 변경합니다. */
    fun markAsOrdered(): CartItem {
        return copy(orderedAt = LocalDateTime.now())
    }

    private fun copy(
            shoppingSession: ShoppingSession = this.shoppingSession,
            retailer: User = this.retailer,
            wholesaler: User? = this.wholesaler,
            flower: Flower = this.flower,
            quantity: Int = this.quantity,
            grade: String = this.grade,
            orderedAt: LocalDateTime? = this.orderedAt,
            id: Long? = this.id
    ) =
            CartItem(
                    shoppingSession = shoppingSession,
                    retailer = retailer,
                    wholesaler = wholesaler,
                    flower = flower,
                    quantity = quantity,
                    grade = grade,
                    orderedAt = orderedAt,
                    id = id
            )
}
