package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import javax.persistence.*

@Entity
@Table(name = "user_credentials")
class UserCredentialJpo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id")
    val userJpo: UserJpo? = null,

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val role: AccountRole? = null,

    @Column(name = "phone_no")
    val phoneNo: String = "",

    @Column(name = "password")
    var password: String = "",
) : BaseJpo() {

    companion object {
        fun create(
            userJpo: UserJpo,
            phoneNo: String,
            password: String,
            role: AccountRole,
            id: Long? = null,
        ): UserCredentialJpo {
            require(phoneNo.isNotBlank()) { "phoneNo must not be blank" }
            require(password.isNotBlank()) { "password must not be blank" }
            return UserCredentialJpo(id = id, userJpo = userJpo, phoneNo = phoneNo, password = password, role = role)
        }
    }

    fun updatePassword(newPassword: String) {
        require(newPassword.isNotBlank()) { "password must not be blank" }
        this.password = newPassword
    }
}
