package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo

@Entity
@Table(name = "business_infos")
class BusinessInfoJpo(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
        @OneToOne @JoinColumn(name = "user_id") val userJpo: UserJpo? = null,
        @Column(name = "company_name") val companyName: String = "",
        @Column(name = "company_phone_no") val companyPhoneNo: String = "",
        @Column(name = "employer_name") val employerName: String = "",
        @Column(name = "address") val address: String = "",
        @Column(name = "business_no") val businessNo: String = "",
        @Column(name = "business_main_category") val businessMainCategory: String = "",
        @Column(name = "business_sub_category") val businessSubCategory: String = "",
        @Column(name = "seal_stamp_img_url") val sealStampImgUrl: String = "",
        @Column(name = "bank_account") val bankAccount: String = "",
) : BaseJpo() {

    companion object {
        fun create(
                id: Long? = null,
                userJpo: UserJpo? = null,
                companyName: String = "",
                companyPhoneNo: String = "",
                employerName: String = "",
                address: String = "",
                businessNo: String = "",
                businessMainCategory: String = "",
                businessSubCategory: String = "",
                sealStampImgUrl: String = "",
                bankAccount: String = ""
        ): BusinessInfoJpo {
            return BusinessInfoJpo(
                    id = id,
                    userJpo = userJpo,
                    companyName = companyName,
                    companyPhoneNo = companyPhoneNo,
                    employerName = employerName,
                    address = address,
                    businessNo = businessNo,
                    businessMainCategory = businessMainCategory,
                    businessSubCategory = businessSubCategory,
                    sealStampImgUrl = sealStampImgUrl,
                    bankAccount = bankAccount
            )
        }
    }
}
