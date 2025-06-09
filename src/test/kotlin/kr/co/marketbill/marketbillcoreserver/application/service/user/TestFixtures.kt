package kr.co.marketbill.marketbillcoreserver.application.service.user

import kr.co.marketbill.marketbillcoreserver.domain.entity.user.*
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus

object TestFixtures {
    fun user(
        id: Long? = null,
        name: String = "user",
        belongsTo: String? = null,
        credential: UserCredential? = null
    ): User {
        return User(
            id = id,
            name = name,
            belongsTo = belongsTo,
            userCredential = credential
        )
    }

    fun credential(
        id: Long? = null,
        user: User? = null,
        phone: String = "01000000000",
        password: String = "password",
        role: AccountRole = AccountRole.RETAILER
    ): UserCredential {
        return UserCredential(
            id = id,
            user = user,
            role = role,
            phoneNo = phone,
            password = password
        )
    }

    fun bizConnection(
        id: Long? = null,
        retailer: User? = null,
        wholesaler: User? = null,
        status: ApplyStatus = ApplyStatus.APPLYING
    ): BizConnection {
        return BizConnection(
            id = id,
            retailer = retailer,
            wholesaler = wholesaler,
            applyStatus = status
        )
    }
}
