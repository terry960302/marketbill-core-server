package kr.co.marketbill.marketbillcoreserver.legacy.domain.entity.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.shared.constants.FlowerGrade
import javax.persistence.*

/**
 * 고객(도매상)이 '직접 입력'해서 추가할 수 있는 항목(OrderItem 과 테이블 분리)
 */
@Entity
@Table(name = "custom_order_items")
data class CustomOrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "order_sheet_id")
    val orderSheet: OrderSheet? = null,

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    var retailer: User? = null,

    @ManyToOne
    @JoinColumn(name = "wholesaler_id")
    var wholesaler: User? = null,

    @Column(name = "flower_name", nullable = true)
    var flowerName: String? = null,

    @Column(name = "flower_type_name", nullable = true)
    var flowerTypeName: String? = null,

    @Column(name = "quantity", nullable = true)
    val quantity: Int? = null,

    @Column(name = "grade", nullable = true)
    val grade: String? = null,

    @Transient
    var gradeValue: FlowerGrade? = null,

    @Column(name = "price", nullable = true)
    var price: Int? = null,
) : SoftDeleteEntity() {
    @PostLoad
    @PostUpdate
    fun postLoad() {
        retailer = if (retailer?.deletedAt == null) retailer else null
        wholesaler = if (wholesaler?.deletedAt == null) wholesaler else null
    }
}
