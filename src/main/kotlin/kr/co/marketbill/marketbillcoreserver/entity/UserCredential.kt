package kr.co.marketbill.marketbillcoreserver.entity

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "user_credentials")
@SQLDelete(sql = "UPDATE entity_class SET deleted_at = current_timestamp WHERE entityId = ?")
@Where(clause = "deleted_at is Null")
data class UserCredential(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    val role : AccountRole? = null,

    @Column(name = "phone_no")
    val phoneNo: String = "",

    @Column(name = "password")
    val password: String = "",

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: LocalDateTime? = null,
) {
}