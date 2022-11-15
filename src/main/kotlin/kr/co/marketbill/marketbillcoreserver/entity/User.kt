package kr.co.marketbill.marketbillcoreserver.entity

import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE entity_class SET deleted_at = current_timestamp WHERE entityId = ?")
@Where(clause = "deleted_at is Null")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "name")
    val name: String = "",

    @Column(name = "business_no", nullable = true)
    val businessNo: String? = null,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val userCredential: UserCredential? = null,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val authToken: AuthToken? = null,

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: LocalDateTime? = null
) {

}