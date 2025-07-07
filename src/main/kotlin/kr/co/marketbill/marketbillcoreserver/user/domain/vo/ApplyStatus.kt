package kr.co.marketbill.marketbillcoreserver.user.domain.vo

enum class ApplyStatus {
    APPLYING,
    CONFIRMED,
    REJECTED,
    ;

    companion object {
        fun from(status: kr.co.marketbill.marketbillcoreserver.types.ApplyStatus): ApplyStatus {
            return ApplyStatus.valueOf(status.name)
        }
    }
} 