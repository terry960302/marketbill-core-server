package kr.co.marketbill.marketbillcoreserver.user.domain.model

import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserCredentialJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.*

data class UserCredential(
        val id: UserCredentialId? = null,
        val userId: UserId? = null,
        val role: AccountRole? = null,
        val phoneNo: PhoneNumber,
        val password: Password
) {
    init {
        require(phoneNo.value.isNotBlank()) { "전화번호는 비어있을 수 없습니다." }
        require(password.value.isNotBlank()) { "비밀번호는 비어있을 수 없습니다." }
    }

    fun updatePassword(newPassword: Password): UserCredential {
        require(newPassword.value.isNotBlank()) { "비밀번호는 비어있을 수 없습니다." }
        return copy(password = newPassword)
    }

    companion object {
        fun create(
                id: Long? = null,
                userId: UserId?,
                phoneNo: String,
                password: String,
                role: AccountRole,
        ): UserCredential {
            return UserCredential(
                    id = id?.let { UserCredentialId.from(it) },
                    userId = userId,
                    role = role,
                    phoneNo = PhoneNumber.from(phoneNo),
                    password = Password.from(password)
            )
        }

        fun fromJpo(jpo: UserCredentialJpo): UserCredential {
            return UserCredential(
                    id = jpo.id?.let { UserCredentialId.from(it) },
                    userId = jpo.userJpo?.id?.let { UserId.from(it) },
                    role = jpo.role,
                    phoneNo = PhoneNumber.from(jpo.phoneNo),
                    password = Password.from(jpo.password),
            )
        }

        fun toJpo(domain: UserCredential): UserCredentialJpo {
            return UserCredentialJpo(
                    id = domain.id?.value,
                    userJpo = null, // 순환 참조 방지를 위해 null로 설정
                    role = domain.role,
                    phoneNo = domain.phoneNo.value,
                    password = domain.password.value
            )
        }
    }
}
