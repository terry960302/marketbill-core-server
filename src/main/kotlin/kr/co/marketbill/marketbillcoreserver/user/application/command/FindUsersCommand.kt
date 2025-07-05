package kr.co.marketbill.marketbillcoreserver.user.application.command

data class FindUsersCommand(val userIds: Set<Long>) {
    companion object {
        fun from(userIds: Set<Long>): FindUsersCommand {
            return FindUsersCommand(userIds)
        }
    }
}