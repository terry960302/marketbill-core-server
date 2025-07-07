package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import javax.persistence.*

// 품목
@Entity
@Table(name = "flower_types")
class FlowerTypeJpo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "img_url", length = 255)
    var imgUrl: String? = null,

    @OneToMany(
        mappedBy = "flowerTypeJpo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.REMOVE],
        orphanRemoval = true
    )
    val flowers: MutableList<FlowerJpo> = mutableListOf()
) : BaseJpo() {

    companion object {
        fun create(name: String, imgUrl: String?): FlowerTypeJpo {
            require(name.isNotBlank()) { "꽃 품목(type)명은 필수입니다." }
            return FlowerTypeJpo(
                name = name,
                imgUrl = imgUrl
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlowerTypeJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
