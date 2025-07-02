package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "auth_tokens")
class AuthTokenJpo protected constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "refresh_token")
    var refreshToken: String = "",

    @OneToOne
    @JoinColumn(name = "user_id")
    val userJpo: UserJpo? = null,
) : BaseJpo() {

    companion object {
        fun create(refreshToken: String, userJpo: UserJpo? = null): AuthTokenJpo {
            require(refreshToken.isNotBlank()) { "refreshToken must not be blank" }
            return AuthTokenJpo(refreshToken = refreshToken, userJpo = userJpo)
        }
    }

    fun updateRefreshToken(newToken: String) {
        require(newToken.isNotBlank()) { "refreshToken must not be blank" }
        this.refreshToken = newToken
    }
}