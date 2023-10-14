package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "auction_results")
@Where(clause = "deleted_at is Null")
data class AuctionResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "flower_name")
    val flowerName: String? = null,

    @Column(name = "flower_type_name")
    val flowerTypeName: String? = null,

    @Column(name = "flower_grade")
    val flowerGrade: String? = null,

    @Column(name = "box_count")
    val boxCount: Int? = null,

    @Column(name = "flower_count")
    val flowerCount: Int? = null,

    @Column(name = "price")
    val price: Int? = null,

    @Column(name = "total_price")
    val totalPrice: Int? = null,

    @Column(name = "serial_code")
    val serialCode: String? = null,

    @Column(name = "wholesaler_id")
    val wholesalerId: Long? = null,

    @Column(name = "auction_date")
    val auctionDate: Int? = null,

    @Column(name = "retail_price")
    val retailPrice: Int? = null,

    @Column(name = "is_sold_out")
    val isSoldOut: Boolean = false,

    @Transient
    @Type(type = "list-string")
    val images: List<String> = emptyList(),
): BaseTime()