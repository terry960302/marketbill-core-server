package kr.co.marketbill.marketbillcoreserver.entity

import kr.co.marketbill.marketbillcoreserver.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "cart_items")
@SQLDelete(sql = "UPDATE cart_items SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class CartItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    val retailer: User? = null,

    @ManyToOne
    @JoinColumn(name = "flower_id")
    val flower: Flower? = null,

    @Column(name = "quantity")
    val quantity: Int? = null,

    @Column(name = "grade")
    val grade: String? = null,

    @Column(name = "ordered_at")
    val orderedAt: LocalDateTime? = null,
) : BaseTime()
