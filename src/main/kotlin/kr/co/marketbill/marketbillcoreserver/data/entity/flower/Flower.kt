package kr.co.marketbill.marketbillcoreserver.data.entity.flower

import kr.co.marketbill.marketbillcoreserver.data.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.data.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.data.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

// TODO : 테스트용 엔티티(설계 보완해서 다시 짤 수도 있음)
@Entity
@Table(name = "flowers")
@SQLDelete(sql = "UPDATE flowers SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
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
    val biddingFlowers: List<BiddingFlower> = arrayListOf(),

    @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY)
    val cartItems: List<CartItem> = arrayListOf(),

    @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY)
    val orderItems: List<OrderItem> = arrayListOf(),


    ) : BaseTime() {
}