package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*


@Entity
@Table(name = "business_infos")
@SQLDelete(sql = "UPDATE business_infos SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class BusinessInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @Column(name = "company_name")
    val companyName: String = "",

    @Column(name = "company_phone_no")
    val companyPhoneNo: String = "",

    // 대표자명
    @Column(name = "employer_name")
    val employerName: String = "",

    @Column(name = "address")
    val address: String = "",

    @Column(name = "business_no")
    val businessNo: String = "",

    // 업태
    @Column(name = "business_main_category")
    val businessMainCategory: String = "",

    // 종목
    @Column(name = "business_sub_category")
    val businessSubCategory: String = "",

    // 인감도장
    @Column(name = "seal_stamp_img_url")
    val sealStampImgUrl: String = "",

    // 계좌
    @Column(name="bank_account")
    val bankAccount : String = "",
) : BaseTime()