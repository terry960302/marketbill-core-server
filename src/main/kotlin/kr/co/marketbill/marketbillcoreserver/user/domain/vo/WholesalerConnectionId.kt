package kr.co.marketbill.marketbillcoreserver.user.domain.vo

data class WholesalerConnectionId(val value: Long) {
    companion object {
        fun from(value: Long): WholesalerConnectionId = WholesalerConnectionId(value)
    }
}
