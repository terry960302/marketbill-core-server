package kr.co.marketbill.marketbillcoreserver.entity.order

import kr.co.marketbill.marketbillcoreserver.entity.common.BaseTime
import kr.co.marketbill.marketbillcoreserver.entity.user.User
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

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    val retailer: User? = null,

    @ManyToOne
    @JoinColumn(name = "wholesaler_id")
    val wholesaler: User? = null,

    @OneToMany(mappedBy = "orderSheet")
    val orderItems : List<OrderItem> = arrayListOf(),

    @OneToMany(mappedBy = "orderSheet")
    val orderSheetReceipts: List<OrderSheetReceipt> = arrayListOf(),

    ) : BaseTime() {
}