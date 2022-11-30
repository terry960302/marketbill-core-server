package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Table(name = "wholesaler_connections")
@SQLDelete(sql = "UPDATE wholesaler_connections SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class WholesalerConnection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 도매상 사장
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_id")
    val employer: User? = null,

    // 도매상 직원
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    val employee: User? = null,
    ) : BaseTime() {
}