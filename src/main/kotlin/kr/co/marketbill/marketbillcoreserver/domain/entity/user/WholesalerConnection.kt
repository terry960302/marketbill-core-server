package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import javax.persistence.*

@Entity
@Table(name = "wholesaler_connections")
data class WholesalerConnection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 도매상 사장
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_id", nullable = false)
    val employer: User? = null,

    // 도매상 직원
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    val employee: User? = null,
) : SoftDeleteEntity() {
}