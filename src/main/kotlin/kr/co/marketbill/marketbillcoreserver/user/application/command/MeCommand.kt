package kr.co.marketbill.marketbillcoreserver.user.application.command


data class MeCommand(val userId: Long) {
    companion object {
        fun fromUserId(userId: Long): MeCommand {
            return MeCommand(userId)
        }

    }
}
