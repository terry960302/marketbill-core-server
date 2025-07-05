package kr.co.marketbill.marketbillcoreserver.user.domain.model

import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.BizConnectionJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.BizConnectionId

/** 거래처 관계 도메인 모델 */
data class BizConnection(
        val id: BizConnectionId? = null,
        val retailer: User? = null,
        val wholesaler: User? = null,
        val applyStatus: ApplyStatus? = null
) {
    companion object {
        fun fromJpo(jpo: BizConnectionJpo): BizConnection =
                BizConnection(
                        id = jpo.id?.let { BizConnectionId.from(it) },
                        retailer = jpo.retailer?.let { User.fromJpo(it) },
                        wholesaler = jpo.wholesaler?.let { User.fromJpo(it) },
                        applyStatus = jpo.applyStatus
                )

        fun toJpo(domain: BizConnection): BizConnectionJpo {
            return BizConnectionJpo(
                    id = domain.id?.value,
                    retailer = domain.retailer?.let { User.toJpo(it) },
                    wholesaler = domain.wholesaler?.let { User.toJpo(it) },
                    applyStatus = domain.applyStatus
            )
        }
    }
}
