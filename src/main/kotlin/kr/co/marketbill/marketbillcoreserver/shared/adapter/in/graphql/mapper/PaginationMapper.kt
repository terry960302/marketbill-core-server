package kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.mapper

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

fun PaginationInput?.toPageInfo(): PageInfo =
    PageInfo(
        page = this?.page ?: 0,
        size = this?.size ?: 15
    )