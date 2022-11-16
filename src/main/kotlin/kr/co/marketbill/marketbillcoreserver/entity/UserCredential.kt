package kr.co.marketbill.marketbillcoreserver.entity

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.entity.common.BaseTime
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "user_credentials")
@SQLDelete(sql = "UPDATE user_credentials SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class UserCredential(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
) : BaseTime()