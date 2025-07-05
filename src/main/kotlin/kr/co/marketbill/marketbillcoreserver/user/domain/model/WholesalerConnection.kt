package kr.co.marketbill.marketbillcoreserver.user.domain.model

import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.WholesalerConnectionJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.WholesalerConnectionId

/** 도매상 사장-직원 관계 도메인 모델 */
data class WholesalerConnection(
        val id: WholesalerConnectionId? = null,
        val employer: User? = null,
        val employee: User? = null
) {
    companion object {
        fun fromJpo(jpo: WholesalerConnectionJpo): WholesalerConnection =
                WholesalerConnection(
                        id = jpo.id?.let { WholesalerConnectionId.from(it) },
                        employer = jpo.employer?.let { User.fromJpo(it) },
                        employee = jpo.employee?.let { User.fromJpo(it) }
                )

        fun toJpo(domain: WholesalerConnection): WholesalerConnectionJpo =
                WholesalerConnectionJpo(
                        id = domain.id?.value,
                        employer = domain.employer?.let { User.toJpo(it) },
                        employee = domain.employee?.let { User.toJpo(it) }
                )
    }
}
