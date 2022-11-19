package kr.co.marketbill.marketbillcoreserver.data.entity.user

import kr.co.marketbill.marketbillcoreserver.data.entity.common.BaseTime
import org.hibernate.annotations.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "auth_tokens")
@SQLDelete(sql = "UPDATE auth_tokens SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class AuthToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "refresh_token")
    val refreshToken: String = "",

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,
) : BaseTime() {
}