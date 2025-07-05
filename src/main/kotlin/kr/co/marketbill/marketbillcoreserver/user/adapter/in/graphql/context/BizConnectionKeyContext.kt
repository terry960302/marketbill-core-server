package kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.context

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.ApplyStatus

data class BizConnectionKeyContext(val status: List<ApplyStatus>?, val pageInfo: PageInfo) {

    companion object {
        fun from(status: List<ApplyStatus>?, pageInfo: PageInfo): BizConnectionKeyContext {
            return BizConnectionKeyContext(status, pageInfo)
        }
    }

}