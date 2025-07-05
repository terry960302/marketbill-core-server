package kr.co.marketbill.marketbillcoreserver.user.domain.model

import java.time.LocalDate
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.BusinessInfoJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.*

data class BusinessInfo(
        val id: BusinessInfoId?,
        val userId: UserId,
        val companyName: CompanyName,
        val companyPhoneNo: CompanyPhoneNo,
        val employerName: EmployerName,
        val address: Address,
        val businessNo: BusinessNo,
        val businessMainCategory: BusinessMainCategory,
        val businessSubCategory: BusinessSubCategory,
        val sealStampImgUrl: SealStampImgUrl,
        val bankAccount: BankAccount,
        val createdAt: LocalDate,
        val updatedAt: LocalDate,
        val deletedAt: LocalDate,
) {
    init {
        require(companyName.value != employerName.value) { "회사명과 대표자명은 달라야 합니다." }
    }

    fun isCategory(main: String, sub: String): Boolean =
            businessMainCategory.value.equals(main, ignoreCase = true) &&
                    businessSubCategory.value.equals(sub, ignoreCase = true)

    fun isComplete(): Boolean =
            companyName.value.isNotBlank() &&
                    companyPhoneNo.value.isNotBlank() &&
                    employerName.value.isNotBlank() &&
                    address.value.isNotBlank() &&
                    businessNo.value.isNotBlank() &&
                    businessMainCategory.value.isNotBlank() &&
                    businessSubCategory.value.isNotBlank() &&
                    sealStampImgUrl.value.isNotBlank() &&
                    bankAccount.value.isNotBlank()

    fun isLocatedIn(city: String): Boolean = address.value.contains(city)

    fun hasSealStamp(): Boolean = sealStampImgUrl.value.isNotBlank()

    companion object {
        fun fromJpo(jpo: BusinessInfoJpo): BusinessInfo {
            return BusinessInfo(
                    id = jpo.id?.let { BusinessInfoId.from(it) },
                    userId = jpo.userJpo?.id?.let { UserId.from(it) } ?: error("userId is null"),
                    companyName = CompanyName.from(jpo.companyName),
                    companyPhoneNo = CompanyPhoneNo.from(jpo.companyPhoneNo),
                    employerName = EmployerName.from(jpo.employerName),
                    address = Address.from(jpo.address),
                    businessNo = BusinessNo.from(jpo.businessNo),
                    businessMainCategory = BusinessMainCategory.from(jpo.businessMainCategory),
                    businessSubCategory = BusinessSubCategory.from(jpo.businessSubCategory),
                    sealStampImgUrl = SealStampImgUrl.from(jpo.sealStampImgUrl),
                    bankAccount = BankAccount.from(jpo.bankAccount),
                    createdAt = jpo.createdAt.toLocalDate(),
                    updatedAt = jpo.updatedAt.toLocalDate(),
                    deletedAt = jpo.deletedAt?.toLocalDate() ?: LocalDate.now()
            )
        }

        fun toJpo(domain: BusinessInfo): BusinessInfoJpo {
            return BusinessInfoJpo(
                    id = domain.id?.value,
                    userJpo = null, // 순환 참조 방지를 위해 null로 설정
                    companyName = domain.companyName.value,
                    companyPhoneNo = domain.companyPhoneNo.value,
                    employerName = domain.employerName.value,
                    address = domain.address.value,
                    businessNo = domain.businessNo.value,
                    businessMainCategory = domain.businessMainCategory.value,
                    businessSubCategory = domain.businessSubCategory.value,
                    sealStampImgUrl = domain.sealStampImgUrl.value,
                    bankAccount = domain.bankAccount.value
            )
        }
    }
}
