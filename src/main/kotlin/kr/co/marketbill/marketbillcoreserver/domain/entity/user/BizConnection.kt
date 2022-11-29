package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Table(name = "biz_connections")
@SQLDelete(sql = "UPDATE biz_connections SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class BizConnection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

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
) : BaseTime() {
}