package kr.co.marketbill.marketbillcoreserver.dto

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole

data class SignInDto(
    val phoneNo : String,
    val password : String,
    val role : AccountRole
) {
}