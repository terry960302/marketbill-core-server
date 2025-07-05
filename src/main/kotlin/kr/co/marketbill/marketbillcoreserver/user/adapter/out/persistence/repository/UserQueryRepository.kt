package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.model.BizConnection
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.PhoneNumber
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserQueryRepository {
    fun findUsersWithDynamicQuery(
        roles: List<AccountRole>?,
        name: String?,
        phoneNo: PhoneNumber?,
        pageable: Pageable
    ): Page<User>

    fun findConnectableUsers(
        currentUserId: UserId,
        currentUserRole: AccountRole,
        pageable: Pageable
    ): Page<User>

    fun findAppliedConnectionsByRetailerIds(
        retailerIds: Set<UserId>,
        status: List<ApplyStatus>?,
        pageable: Pageable
    ): Page<BizConnection>

    fun findReceivedConnectionsByWholesalerIds(
        wholesalerIds: Set<UserId>,
        status: List<ApplyStatus>?,
        pageable: Pageable
    ): Page<BizConnection>

    fun findEmployeesByEmployerIds(employerIds: Set<UserId>): List<User>
    fun findEmployersByEmployeeIds(employeeIds: Set<UserId>): List<User>
}
