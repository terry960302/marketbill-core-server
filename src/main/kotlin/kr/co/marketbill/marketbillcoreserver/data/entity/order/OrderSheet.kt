package kr.co.marketbill.marketbillcoreserver.data.entity.order

import kr.co.marketbill.marketbillcoreserver.data.entity.common.BaseTime
import kr.co.marketbill.marketbillcoreserver.data.entity.user.User
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Table(name = "order_sheets")
@SQLDelete(sql = "UPDATE order_sheets SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class OrderSheet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "order_no")
    val orderNo: String = "",

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    val retailer: User? = null,

    @ManyToOne
    @JoinColumn(name = "wholesaler_id")
    val wholesaler: User? = null,

    @OneToMany(mappedBy = "orderSheet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orderItems: List<OrderItem> = arrayListOf(),

    @OneToMany(mappedBy = "orderSheet")
    val orderSheetReceipts: List<OrderSheetReceipt> = arrayListOf(),

    @Transient
    var totalFlowerQuantity: Int = 0,

    @Transient
    var totalFlowerTypeCount: Int = 0,
) : BaseTime() {
    @PostLoad
    fun postLoad() {
        totalFlowerQuantity = if (orderItems.isNotEmpty()) {
            val quantities: List<Int> = orderItems.map { if (it.quantity == null) 0 else it.quantity!! }
            quantities.reduce { acc, i -> acc + i }
        } else {
            0
        }

        totalFlowerTypeCount = if (orderItems.isNotEmpty()) {
            val flowerTypes: List<Long> = orderItems.mapNotNull { it.flower?.flowerType?.id }.distinct()
            flowerTypes.count()
        } else {
            0
        }
    }
}