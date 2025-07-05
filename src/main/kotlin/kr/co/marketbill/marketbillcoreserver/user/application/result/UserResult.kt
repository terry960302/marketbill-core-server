package kr.co.marketbill.marketbillcoreserver.user.application.result

import kr.co.marketbill.marketbillcoreserver.user.domain.model.BusinessInfo
import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.*

data class UserResult(
    val id: UserId?,
    val name: UserName,
    val belongsTo: BelongsTo?,
    val phoneNumber: PhoneNumber,
    val businessInfo: BusinessInfo?,
    val role: AccountRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?
) {
    companion object {
        fun from(user: User): UserResult {
            return UserResult(
                id = user.id,
                name = user.name,
                belongsTo = user.belongsTo,
                phoneNumber = user.phoneNumber,
                role = user.role,
                businessInfo = user.businessInfo,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                deletedAt = user.deletedAt,
            )
        }
    }
}
