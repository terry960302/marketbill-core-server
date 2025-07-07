package kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.mapper

import kr.co.marketbill.marketbillcoreserver.types.AuthToken
import kr.co.marketbill.marketbillcoreserver.types.User
import kr.co.marketbill.marketbillcoreserver.types.UserCredential
import kr.co.marketbill.marketbillcoreserver.user.application.result.AuthTokenResult
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserSearchResult
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import org.springframework.stereotype.Component

@Component
class UserOutputMapper(private val businessInfoMapper: BusinessInfoOutputMapper) {

    fun toUser(result: UserResult): User {
        return User(
            id = result.id?.value?.toInt() ?: 0,
            belongsTo = result.belongsTo?.value,
            name = result.name.value,
            businessInfo = result.businessInfo?.let { businessInfoMapper.toOutput(result.businessInfo) },
            userCredential =
            UserCredential(
                id = result.id?.value?.toInt() ?: 0,
                user = null,
                role = AccountRole.toOutput(result.role),
                phoneNo = result.phoneNumber.value,
                createdAt = result.createdAt.toLocalDate(),
                deletedAt = result.deletedAt?.toLocalDate()
            ),
            appliedConnections = emptyList(),
            receivedConnections = emptyList(),
            connectedEmployees = emptyList(),
            connectedEmployer = null,
            deletedAt = result.deletedAt?.toLocalDate()
        )
    }

    fun toUsers(result: UserSearchResult): List<User> {
        return result.users.map { toUser(it) }
    }

    fun toAuthToken(result: AuthTokenResult): AuthToken {
        return AuthToken(accessToken = result.accessToken, refreshToken = result.refreshToken)
    }
}
