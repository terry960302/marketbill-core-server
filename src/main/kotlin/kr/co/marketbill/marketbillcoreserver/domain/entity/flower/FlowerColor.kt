package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

// 색상
@Entity
@Table(name = "flower_colors")
@SQLDelete(sql = "UPDATE flower_colors SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class FlowerColor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name")
    val name: String = "",

    ) : BaseTime() {
}