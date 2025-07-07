package kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.mapper

import kr.co.marketbill.marketbillcoreserver.user.domain.model.BusinessInfo
import org.springframework.stereotype.Component

@Component
class BusinessInfoOutputMapper {
    fun toOutput(domain: BusinessInfo): kr.co.marketbill.marketbillcoreserver.types.BusinessInfo {
        return kr.co.marketbill.marketbillcoreserver.types.BusinessInfo(
            id = domain.id!!.value.toInt(),
            companyName = domain.companyName.value,
            companyPhoneNo = domain.companyPhoneNo.value,
            employerName = domain.employerName.value,
            address = domain.address.value,
            businessNo = domain.businessNo.value,
            businessMainCategory = domain.businessMainCategory.value,
            businessSubCategory = domain.businessSubCategory.value,
            sealStampImgUrl = domain.sealStampImgUrl.value,
            bankAccount = domain.bankAccount.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt,
        )
    }
}