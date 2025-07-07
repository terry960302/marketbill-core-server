package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.ApplyStatus

@Entity
@Table(name = "biz_connections")
data class BizConnectionJpo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,

    // 거래처 관계 신청자
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "retailer_id")
    val retailer: UserJpo? = null,

    // 거래처 관계 수취자
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wholesaler_id")
    val wholesaler: UserJpo? = null,

    @Column(name = "apply_status")
    @Enumerated(EnumType.STRING)
    var applyStatus: ApplyStatus? = null,
) : BaseJpo() {}
