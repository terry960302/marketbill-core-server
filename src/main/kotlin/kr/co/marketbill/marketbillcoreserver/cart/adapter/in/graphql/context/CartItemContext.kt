package kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.context

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.context.BizConnectionContext

data class CartItemContext(val pageInfo: PageInfo) {

    companion object {
        fun from(paginationInput: PaginationInput?): CartItemContext? {
            val pageInfo = PageInfo.from(
                paginationInput?.page,
                paginationInput?.size
            ) ?: return null

            return CartItemContext(pageInfo)
        }
    }

}