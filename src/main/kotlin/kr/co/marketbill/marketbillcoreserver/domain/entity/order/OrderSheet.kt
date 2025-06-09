package kr.co.marketbill.marketbillcoreserver.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "order_sheets")
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
    val customOrderItems: List<CustomOrderItem> = listOf(),

    @OneToMany(mappedBy = "orderSheet", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val orderSheetReceipts: List<OrderSheetReceipt> = listOf(),

    @Column(name = "price_updated_at", nullable = true)
    var priceUpdatedAt: LocalDateTime? = null,

    @Column(name = "memo", nullable = true, length = 600)
    var memo: String? = null,

    @Transient
    var totalFlowerQuantity: Int = 0,

    @Transient
    var totalFlowerTypeCount: Int = 0,

    @Transient
    var hasReceipt: Boolean = false,

    @Transient
    var recentReceipt: OrderSheetReceipt? = null,

    @Transient
    var isPriceUpdated: Boolean = false,
) : SoftDeleteEntity() {

    @PostLoad
    @PostUpdate
    fun postLoad() {
        retailer = if (retailer?.deletedAt == null) retailer else null
        wholesaler = if (wholesaler?.deletedAt == null) wholesaler else null
        isPriceUpdated = priceUpdatedAt != null
    }
}