package kr.co.marketbill.marketbillcoreserver.user.application.command

import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.BelongsTo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.Password
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.PhoneNumber
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserName

data class SignUpCommand(
    val name: String,
    val phoneNumber: String,
    val password: String,
    val role: AccountRole,
    val belongsTo: String? = null
) {
    companion object {
        fun fromGraphql(
            name: String,
            phoneNumber: String,
            password: String,
            role: AccountRole,
            belongsTo: String? = null
        ): SignUpCommand {
            return SignUpCommand(
                name = name,
                phoneNumber = phoneNumber,
                password = password,
                role = role,
                belongsTo = belongsTo
            )
        }
    }
}
