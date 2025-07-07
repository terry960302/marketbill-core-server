package kr.co.marketbill.marketbillcoreserver.user.application.command

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole

data class UserSearchCommand(val criteria: UserSearchCriteria, val pageInfo: PageInfo) {

    fun canSearchConnectable(): Boolean = criteria.canSearchConnectable()

    companion object {
        fun fromGraphql(
            currentUserId: Long? = null,
            currentUserRole: AccountRole? = null,
            roles: List<AccountRole>? = null,
            phoneNumber: String? = null,
            name: String? = null,
            connectable: Boolean? = null,
            page: Int = 0,
            size: Int = 20
        ): UserSearchCommand {
            return UserSearchCommand(
                criteria =
                UserSearchCriteria.from(
                    currentUserId = currentUserId,
                    currentUserRole = currentUserRole,
                    roles =
                    roles?.map {
                        AccountRole.valueOf(it.toString())
                    },
                    phoneNumber = phoneNumber,
                    name = name,
                    connectable = connectable,
                ),
                pageInfo = PageInfo.from(page, size)!!
            )
        }
    }
}
