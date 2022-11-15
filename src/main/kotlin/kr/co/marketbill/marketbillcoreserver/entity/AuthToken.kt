package kr.co.marketbill.marketbillcoreserver.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "auth_tokens")
@SQLDelete(sql = "UPDATE entity_class SET deleted_at = current_timestamp WHERE entityId = ?")
@Where(clause = "deleted_at is Null")
data class AuthToken(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "refresh_token")
    val refreshToken: String = "",

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true)
    val updatedAt: LocalDateTime? = null,

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: LocalDateTime? = null,


    ) {
}