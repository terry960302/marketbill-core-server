package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import javax.persistence.*

@Entity
@Table(name = "wholesaler_connections")
data class WholesalerConnectionJpo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 도매상 사장
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_id", nullable = false)
    val employer: UserJpo? = null,

    // 도매상 직원
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    val employee: UserJpo? = null,
) : BaseJpo() {
}