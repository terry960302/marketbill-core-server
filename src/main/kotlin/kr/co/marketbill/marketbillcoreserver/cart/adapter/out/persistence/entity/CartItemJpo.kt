package kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerJpo
import javax.persistence.*
import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo

@Entity
@Table(name = "cart_item")
class CartItemJpo(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "shopping_session_id", nullable = false)
        val shoppingSessionJpo: ShoppingSessionJpo? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "retailer_id", nullable = false)
        val retailerJpo: UserJpo? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "wholesaler_id")
        val wholesalerJpo: UserJpo? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "flower_id", nullable = false)
        val flowerJpo: FlowerJpo? = null,

        @Column(name = "quantity", nullable = false)
        val quantity: Int,

        @Enumerated(EnumType.STRING)
        @Column(name = "grade", nullable = false)
        val grade: FlowerGrade,

        @Column(name = "memo")
        val memo: String?,

        @Column(name = "ordered_at")
        val orderedAt: LocalDateTime?
) : BaseJpo() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CartItemJpo

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
