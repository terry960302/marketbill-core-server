package kr.co.marketbill.marketbillcoreserver.user.application.command

import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole

data class UserSearchCriteria(
    val currentUserId: Long? = null,
    val currentUserRole: AccountRole? = null,
    val roles: List<AccountRole>? = null,
    val phoneNumber: String? = null,
    val name: String? = null,
    val connectable: Boolean? = null,
    val excludeDeleted: Boolean = true
) {
    fun hasRoleFilter(): Boolean = !roles.isNullOrEmpty()

    fun hasPhoneFilter(): Boolean = phoneNumber != null

    fun hasNameFilter(): Boolean = name != null

    fun hasAnyFilter(): Boolean = hasRoleFilter() || hasPhoneFilter() || hasNameFilter()

    fun canSearchConnectable(): Boolean {
        if (connectable == null || !connectable) return false
        requireNotNull(currentUserId) { "연결가능한 사용자 목록 조회를 위해선 로그인이 필요합니다." }
        requireNotNull(currentUserRole) { "연결가능한 사용자 목록 조회를 위해선 로그인이 필요합니다." }
        return true
    }

    companion object {
        fun from(
            currentUserId: Long? = null,
            currentUserRole: AccountRole? = null,
            roles: List<AccountRole>? = null,
            phoneNumber: String? = null,
            name: String? = null,
            connectable: Boolean? = null,
            excludeDeleted: Boolean = true
        ): UserSearchCriteria {
            return UserSearchCriteria(
                currentUserId = currentUserId,
                currentUserRole = currentUserRole,
                roles = roles,
                phoneNumber = phoneNumber,
                name = name,
                connectable = connectable,
                excludeDeleted = excludeDeleted
            )
        }
    }
}
