package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

// 품목
@Entity
@Table(name = "flower_types")
@SQLDelete(sql = "UPDATE flower_types SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class FlowerType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name")
    val name: String = "",

    @OneToMany(mappedBy = "flowerType", fetch = FetchType.LAZY)
    val flowers: List<Flower> = arrayListOf(),

    @Column(name = "img_url", length = 255, nullable = true)
    val imgUrl: String? = null,

    ) : BaseTime() {
}