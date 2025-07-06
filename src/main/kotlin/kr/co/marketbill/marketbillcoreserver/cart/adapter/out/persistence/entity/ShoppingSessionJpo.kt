package kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.entity

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo

@Entity
@Table(name = "shopping_session")
class ShoppingSessionJpo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_id", nullable = false)
    val retailerJpo: UserJpo? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wholesaler_id")
    val wholesalerJpo: UserJpo? = null,

    @Column(name = "memo")
    val memo: String?
) : BaseJpo() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingSessionJpo

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
