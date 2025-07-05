package kr.co.marketbill.marketbillcoreserver.user.application.command

import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId

data class GetUserCommand(val userId: Long) {
    companion object {
        fun fromGraphql(id: Int): GetUserCommand {
            return GetUserCommand(id.toLong())
        }
        fun fromUserId(userId: Long): GetUserCommand {
            return GetUserCommand(userId)
        }
    }
}

