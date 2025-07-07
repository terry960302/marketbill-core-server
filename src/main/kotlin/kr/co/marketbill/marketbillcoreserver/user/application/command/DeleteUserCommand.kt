package kr.co.marketbill.marketbillcoreserver.user.application.command

import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId

data class DeleteUserCommand(val userId: Long) {
    companion object {
        fun fromGraphql(userId: Int): DeleteUserCommand {
            return DeleteUserCommand(userId.toLong())
        }
    }
}
