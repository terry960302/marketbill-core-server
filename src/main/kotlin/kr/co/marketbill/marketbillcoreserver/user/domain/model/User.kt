package kr.co.marketbill.marketbillcoreserver.user.domain.model

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.*

data class User(
        val id: UserId? = null,
        val name: UserName,
        val belongsTo: BelongsTo? = null,
        val phoneNumber: PhoneNumber,
        val businessInfo: BusinessInfo? = null,
        val credential: UserCredential? = null,
        val password: Password,
        val role: AccountRole,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now(),
        val deletedAt: LocalDateTime? = null,
        val appliedConnections: List<BizConnection> = emptyList(),
        val receivedConnections: List<BizConnection> = emptyList(),
        val wholesalerConnectionsByEmployer: List<WholesalerConnection> = emptyList(),
        val wholesalerConnectionsByEmployee: List<WholesalerConnection> = emptyList(),
        val connectedEmployer: User? = null,
        val connectedEmployees: List<User> = emptyList()
) {
    init {
        require(name.value.isNotBlank()) { "사용자 이름은 비어있을 수 없습니다." }
        require(phoneNumber.value.isNotBlank()) { "전화번호는 비어있을 수 없습니다." }
        require(password.value.isNotBlank()) { "비밀번호는 비어있을 수 없습니다." }
    }

    fun canCreateReceipt(): Boolean {
        if (businessInfo == null) return false
        return businessInfo.isComplete()
    }

    fun isRetailer(): Boolean = role == AccountRole.RETAILER

    fun isWholesaler(): Boolean =
            role == AccountRole.WHOLESALER_EMPR || role == AccountRole.WHOLESALER_EMPE

    fun isWholesalerEmployer(): Boolean = role == AccountRole.WHOLESALER_EMPR

    fun isWholesalerEmployee(): Boolean = role == AccountRole.WHOLESALER_EMPE

    fun canConnectWith(other: User): Boolean {
        return when {
            this.isRetailer() && other.isWholesaler() -> true
            this.isWholesaler() && other.isRetailer() -> true
            else -> false
        }
    }

    fun isDeleted(): Boolean = deletedAt != null

    fun softDelete(): User = copy(deletedAt = LocalDateTime.now())

    fun updatePassword(newPassword: Password): User =
            copy(password = newPassword, updatedAt = LocalDateTime.now())

    fun updateName(newName: UserName): User = copy(name = newName, updatedAt = LocalDateTime.now())

    fun updateBelongsTo(newBelongsTo: BelongsTo?): User =
            copy(belongsTo = newBelongsTo, updatedAt = LocalDateTime.now())

    companion object {
        fun create(
                name: String,
                phoneNumber: String,
                password: String,
                role: AccountRole,
                businessInfo: BusinessInfo?,
                belongsTo: String? = null
        ): User {
            val belongsToValue = belongsTo?.let { BelongsTo.from(it) }

            return User(
                    name = UserName.from(name),
                    phoneNumber = PhoneNumber.from(phoneNumber),
                    password = Password.from(password),
                    role = role,
                    belongsTo = belongsToValue,
                    businessInfo = businessInfo
            )
        }

        fun create(
                id: Long?,
                name: String,
                phoneNumber: String,
                password: String,
                role: AccountRole,
                businessInfo: BusinessInfo?,
                belongsTo: String? = null,
                createdAt: LocalDateTime = LocalDateTime.now(),
                updatedAt: LocalDateTime = LocalDateTime.now(),
                deletedAt: LocalDateTime? = null
        ): User {
            val userId = id?.let { UserId.from(it) }
            val belongsToValue = belongsTo?.let { BelongsTo.from(it) }

            return User(
                    id = userId,
                    name = UserName.from(name),
                    phoneNumber = PhoneNumber.from(phoneNumber),
                    password = Password.from(password),
                    role = role,
                    belongsTo = belongsToValue,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    deletedAt = deletedAt,
                    businessInfo = businessInfo
            )
        }

        fun fromJpo(jpo: UserJpo): User {
            return User(
                    id = jpo.id?.let { UserId.from(it) },
                    name = UserName.from(jpo.name),
                    belongsTo = jpo.belongsTo?.let { BelongsTo.from(it) },
                    phoneNumber = jpo.userCredentialJpo?.phoneNo?.let { PhoneNumber.from(it) }
                                    ?: error("phoneNo is null"),
                    password = jpo.userCredentialJpo?.password?.let { Password.from(it) }
                                    ?: error("password is null"),
                    role = jpo.userCredentialJpo?.role ?: error("UserCredential role is null"),
                    createdAt = jpo.createdAt,
                    updatedAt = jpo.updatedAt,
                    deletedAt = jpo.deletedAt,
                    credential = jpo.userCredentialJpo?.let { UserCredential.fromJpo(it) },
                    appliedConnections =
                            jpo.appliedConnections.mapNotNull { BizConnection.fromJpo(it) },
                    receivedConnections =
                            jpo.receivedConnections.mapNotNull { BizConnection.fromJpo(it) },
                    wholesalerConnectionsByEmployer =
                            jpo.wholesalerConnectionsByEmployerJpo.mapNotNull {
                                WholesalerConnection.fromJpo(it)
                            },
                    wholesalerConnectionsByEmployee =
                            jpo.wholesalerConnectionsByEmployeeJpo.mapNotNull {
                                WholesalerConnection.fromJpo(it)
                            },
            )
        }

        fun toJpo(domain: User): UserJpo {
            return UserJpo(
                    id = domain.id?.value,
                    name = domain.name.value,
                    belongsTo = domain.belongsTo?.value,
                    userCredentialJpo = domain.credential?.let { UserCredential.toJpo(it) },
                    authTokenJpo = null,
                    businessInfoJpo = domain.businessInfo?.let { BusinessInfo.toJpo(it) },
                    appliedConnections =
                            domain.appliedConnections.mapNotNull { BizConnection.toJpo(it) },
                    receivedConnections =
                            domain.receivedConnections.mapNotNull { BizConnection.toJpo(it) },
                    wholesalerConnectionsByEmployerJpo =
                            domain.wholesalerConnectionsByEmployer.mapNotNull {
                                WholesalerConnection.toJpo(it)
                            },
                    wholesalerConnectionsByEmployeeJpo =
                            domain.wholesalerConnectionsByEmployee.mapNotNull {
                                WholesalerConnection.toJpo(it)
                            },
            )
        }
    }
}
