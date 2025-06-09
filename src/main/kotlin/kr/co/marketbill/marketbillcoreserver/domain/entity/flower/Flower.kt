package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import javax.persistence.*
// 품종
@Entity
@Table(name = "flowers")
class Flower(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name")
    val name: String = "",

    @ManyToOne
    @JoinColumn(name = "flower_type_id")
    val flowerType: FlowerType? = null,

    @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY)
    val biddingFlowers: List<BiddingFlower>? = null,

    @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY)
    val cartItems: List<CartItem>? = null,

    @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY)
    val orderItems: List<OrderItem>? = null,
    ) : SoftDeleteEntity() {
}