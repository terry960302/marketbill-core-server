package kr.co.marketbill.marketbillcoreserver.cart.application.command

import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId

data class FindShoppingSessionCommand(
        val retailerId: UserId? = null,
        val retailerIds: Set<UserId>? = null
) {
    companion object {
        fun from(retailerIds: Set<Long>): FindShoppingSessionCommand {
            return FindShoppingSessionCommand(retailerIds = retailerIds.map { UserId(it) }.toSet())
        }
    }
}
