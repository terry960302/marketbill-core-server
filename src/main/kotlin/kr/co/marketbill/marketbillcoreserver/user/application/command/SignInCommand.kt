package kr.co.marketbill.marketbillcoreserver.user.application.command

import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.Password
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.PhoneNumber

data class SignInCommand(
    val phoneNumber: String,
    val password: String,
) {
    companion object {
        fun fromGraphql(
            phoneNumber: String,
            password: String,
        ): SignInCommand {
            return SignInCommand(
                phoneNumber = phoneNumber,
                password = password,
            )
        }
    }
}

