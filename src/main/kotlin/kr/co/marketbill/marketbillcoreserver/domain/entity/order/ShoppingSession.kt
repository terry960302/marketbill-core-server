package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User

@Entity
@Table(name = "shopping_sessions")
class ShoppingSession
private constructor(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @OneToOne @JoinColumn(name = "retailer_id") val retailer: User?,
        @ManyToOne @JoinColumn(name = "wholesaler_id", nullable = true) val wholesaler: User?,
        @Column(name = "memo", nullable = true, length = 600) val memo: String?,
        @OneToMany(
                mappedBy = "shoppingSession",
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.LAZY
        )
        val cartItems: List<CartItem> = listOf()
) : SoftDeleteEntity() {

    companion object {
        fun createWith(
                retailer: User,
                wholesaler: User? = null,
                memo: String? = null
        ): ShoppingSession {
            return ShoppingSession(retailer = retailer, wholesaler = wholesaler, memo = memo)
        }
    }

    /** 도매상을 업데이트합니다. */
    fun updateWholesaler(newWholesaler: User?): ShoppingSession {
        return copy(wholesaler = newWholesaler)
    }

    /** 메모를 업데이트합니다. */
    fun updateMemo(newMemo: String?): ShoppingSession {
        return copy(memo = newMemo)
    }

    /** 장바구니 아이템을 추가합니다. 같은 상품이 있으면 수량을 합산하고, 없으면 새로 추가합니다. */
    fun addCartItem(cartItem: CartItem): ShoppingSession {
        val existingItem = cartItems.find { it.isSameItem(cartItem) }
        return if (existingItem != null) {
            copy(cartItems = cartItems - existingItem + existingItem.mergeWith(cartItem))
        } else {
            copy(cartItems = cartItems + cartItem)
        }
    }

    /** 장바구니 아이템을 삭제합니다. */
    fun removeCartItem(cartItemId: Long): ShoppingSession {
        val updatedItems = cartItems.filter { it.id != cartItemId }
        return copy(cartItems = updatedItems)
    }

    private fun copy(
            id: Long? = this.id,
            retailer: User? = this.retailer,
            wholesaler: User? = this.wholesaler,
            memo: String? = this.memo,
            cartItems: List<CartItem> = this.cartItems
    ) =
            ShoppingSession(
                    id = id,
                    retailer = retailer,
                    wholesaler = wholesaler,
                    memo = memo,
                    cartItems = cartItems
            )
}
