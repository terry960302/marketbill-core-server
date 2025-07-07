package kr.co.marketbill.marketbillcoreserver.user.domain.vo

enum class AccountRole {
    RETAILER, // 소매상
    WHOLESALER_EMPR, // 도매상 고용주(EMPLOYER)
    WHOLESALER_EMPE  // 도매상 고용자(EMPLOYEE)
    ;

    companion object {
        fun toOutput(role: AccountRole): kr.co.marketbill.marketbillcoreserver.types.AccountRole {
            return kr.co.marketbill.marketbillcoreserver.types.AccountRole.valueOf(role.name)
        }

        fun from(role: kr.co.marketbill.marketbillcoreserver.types.AccountRole): AccountRole {
            return AccountRole.valueOf(role.name)
        }
    }
} 