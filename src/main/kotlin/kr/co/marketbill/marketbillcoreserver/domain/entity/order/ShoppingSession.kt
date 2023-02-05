package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Table(name = "shopping_sessions")
@SQLDelete(sql = "UPDATE shopping_sessions SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class ShoppingSession(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "retailer_id")
    var retailer: User? = null,

    @OneToOne
    @JoinColumn(name = "wholesaler_id", nullable = true)
    var wholesaler: User? = null,

    @Column(name = "memo", nullable = true, length = 600)
    var memo: String? = null,

    @OneToMany(mappedBy = "shoppingSession", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val cartItems: List<CartItem> = listOf(),
) : BaseTime() {
    @PostLoad
    @PostUpdate
    fun postLoad() {
        retailer = if (retailer?.deletedAt == null) retailer else null
        wholesaler = if (wholesaler?.deletedAt == null) wholesaler else null
    }
}