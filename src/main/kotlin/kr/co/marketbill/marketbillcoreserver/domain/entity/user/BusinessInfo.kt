package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import javax.persistence.*

@Entity
@Table(name = "business_infos")
class BusinessInfo protected constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,

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
) : SoftDeleteEntity() {

    companion object {
        fun create(
            user: User,
            companyName: String,
            companyPhoneNo: String,
            employerName: String,
            address: String,
            businessNo: String,
            businessMainCategory: String,
            businessSubCategory: String,
            sealStampImgUrl: String,
            bankAccount: String,
            id: Long? = null,
        ): BusinessInfo {
            require(companyName.isNotBlank()) { "companyName must not be blank" }
            require(companyPhoneNo.isNotBlank()) { "companyPhoneNo must not be blank" }
            require(employerName.isNotBlank()) { "employerName must not be blank" }
            require(address.isNotBlank()) { "address must not be blank" }
            require(businessNo.isNotBlank()) { "businessNo must not be blank" }
            require(businessMainCategory.isNotBlank()) { "businessMainCategory must not be blank" }
            require(businessSubCategory.isNotBlank()) { "businessSubCategory must not be blank" }
            require(sealStampImgUrl.isNotBlank()) { "sealStampImgUrl must not be blank" }
            require(bankAccount.isNotBlank()) { "bankAccount must not be blank" }
            return BusinessInfo(
                id = id,
                user = user,
                companyName = companyName,
                companyPhoneNo = companyPhoneNo,
                employerName = employerName,
                address = address,
                businessNo = businessNo,
                businessMainCategory = businessMainCategory,
                businessSubCategory = businessSubCategory,
                sealStampImgUrl = sealStampImgUrl,
                bankAccount = bankAccount,
            )
        }
    }

    fun assignId(newId: Long?) {
        this.id = newId
    }
}
