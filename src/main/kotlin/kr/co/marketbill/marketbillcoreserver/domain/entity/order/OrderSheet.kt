package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter
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
    var orderNo: String = "",

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    var retailer: User? = null,

    @ManyToOne
    @JoinColumn(name = "wholesaler_id")
    var wholesaler: User? = null,

    @OneToMany(mappedBy = "orderSheet", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val orderItems: List<OrderItem> = listOf(),

    @OneToMany(mappedBy = "orderSheet", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val orderSheetReceipts: List<OrderSheetReceipt> = listOf(),

    @Transient
    var totalFlowerQuantity: Int = 0,

    @Transient
    var totalFlowerTypeCount: Int = 0,

    @Transient
    var hasReceipt: Boolean = false,

    @Transient
    var recentReceipt: OrderSheetReceipt? = null,
) : BaseTime() {

    @PostLoad
    fun postLoad(){
        retailer = if (retailer?.deletedAt == null) retailer else null
        wholesaler = if (wholesaler?.deletedAt == null) wholesaler else null
    }


    fun mapOrderItemRelatedFields() {
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

    fun mapReceiptRelatedFields() {
        hasReceipt = orderSheetReceipts.isEmpty()

        val orderSheetReceiptsSortByDesc: List<OrderSheetReceipt>? =
            orderSheetReceipts.sortedByDescending { it.createdAt }
        recentReceipt = if (orderSheetReceiptsSortByDesc == null || orderSheetReceiptsSortByDesc.isEmpty()) {
            null
        } else {
            orderSheetReceiptsSortByDesc[0]
        }
    }
}