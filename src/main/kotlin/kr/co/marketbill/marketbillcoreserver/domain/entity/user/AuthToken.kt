package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "auth_tokens")
class AuthToken protected constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "refresh_token")
    var refreshToken: String = "",

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,
) : SoftDeleteEntity() {

    companion object {
        fun create(refreshToken: String, user: User? = null): AuthToken {
            require(refreshToken.isNotBlank()) { "refreshToken must not be blank" }
            return AuthToken(refreshToken = refreshToken, user = user)
        }
    }

    fun updateRefreshToken(newToken: String) {
        require(newToken.isNotBlank()) { "refreshToken must not be blank" }
        this.refreshToken = newToken
    }
}