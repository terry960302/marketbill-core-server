package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import javax.persistence.*

@Entity
@Table(name = "business_infos")
class BusinessInfoJpo protected constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id")
    val userJpo: UserJpo? = null,

    @Column(name = "company_name")
    val companyName: String = "",

    @Column(name = "company_phone_no")
    val companyPhoneNo: String = "",

    @Column(name = "employer_name")
    val employerName: String = "",

    @Column(name = "address")
    val address: String = "",

    @Column(name = "business_no")
    val businessNo: String = "",

    @Column(name = "business_main_category")
    val businessMainCategory: String = "",

    @Column(name = "business_sub_category")
    val businessSubCategory: String = "",

    @Column(name = "seal_stamp_img_url")
    val sealStampImgUrl: String = "",

    @Column(name = "bank_account")
    val bankAccount: String = "",
) : BaseJpo() {
}
