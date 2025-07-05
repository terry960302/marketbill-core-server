package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.adapter.out.persistence.types.ListStringType
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.types.FlowerColor
import javax.persistence.*
import org.hibernate.annotations.Type
import org.hibernate.annotations.OrderBy
import org.hibernate.annotations.TypeDef

// 품종
@TypeDef(name = "list-string", typeClass = ListStringType::class)
@Entity
@Table(
    name = "flowers", indexes = [
        Index(name = "idx_flower_created_at", columnList = "created_at"),
        Index(name = "idx_flower_name", columnList = "name"),
        Index(name = "idx_flower_type_id", columnList = "flower_type_id")
    ]
)
class FlowerJpo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_type_id", nullable = false)
    var flowerTypeJpo: FlowerTypeJpo,

    @Type(type = "list-string")
    @Column(name = "images", columnDefinition = "varchar(255)[]")
    @OrderBy(clause = "image desc")
    val images: List<String> = emptyList(),

    @ManyToOne
    @JoinColumn(name = "flower_color_id")
    var flowerColor: FlowerColorJpo,

    @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val biddingFlowerJpos: MutableList<BiddingFlowerJpo> = mutableListOf(),

//    @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
//    val cartItems: MutableList<CartItem> = mutableListOf(),
//
//    @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
//    val orderItems: MutableList<OrderItem> = mutableListOf()
) : BaseJpo() {

    companion object {
        fun create(name: String, type: FlowerTypeJpo, color: FlowerColorJpo, images: List<String>): FlowerJpo {
            require(name.isNotBlank()) { "꽃 이름(품종명)은 필수입니다." }
            return FlowerJpo(
                name = name,
                flowerTypeJpo = type,
                flowerColor = color,
                images = images,
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlowerJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}