package kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request

import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole


data class SignInDto(
    val phoneNo : String,
    val password : String,
    val role : AccountRole
) {
}