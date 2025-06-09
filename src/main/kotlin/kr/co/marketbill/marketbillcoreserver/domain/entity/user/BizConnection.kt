package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus

@Entity
@Table(name = "biz_connections")
data class BizConnection(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,

        // 거래처 관계 신청자
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "retailer_id")
        val retailer: User? = null,

        // 거래처 관계 수취자
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "wholesaler_id")
        val wholesaler: User? = null,
        @Column(name = "apply_status")
        @Enumerated(EnumType.STRING)
        var applyStatus: ApplyStatus? = null,
) : SoftDeleteEntity() {}
