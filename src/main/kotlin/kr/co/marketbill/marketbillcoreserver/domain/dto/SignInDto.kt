package kr.co.marketbill.marketbillcoreserver.domain.dto

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole

data class SignInDto(
    val phoneNo : String,
    val password : String,
    val role : AccountRole
) {
}