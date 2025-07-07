package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "flower_colors")
class FlowerColorJpo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,
) : BaseJpo() {

    companion object {
        fun create(name: String): FlowerColorJpo {
            return FlowerColorJpo(name = name);
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlowerColorJpo) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
