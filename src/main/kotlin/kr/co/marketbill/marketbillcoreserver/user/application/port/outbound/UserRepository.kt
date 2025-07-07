package kr.co.marketbill.marketbillcoreserver.user.application.port.outbound

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import kr.co.marketbill.marketbillcoreserver.user.application.command.UserSearchCriteria
import kr.co.marketbill.marketbillcoreserver.user.domain.model.BizConnection
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.PhoneNumber
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.data.domain.Page

interface UserRepository {
    fun save(user: User): User

    fun findById(id: UserId): User?

    fun findByIds(ids: Set<UserId>): Map<UserId, User>

    fun findByPhoneNumber(phoneNumber: PhoneNumber): User?

    fun search(criteria: UserSearchCriteria, pageInfo: PageInfo): PageResult<User>

    fun findConnectableUsers(
            currentUserId: UserId,
            currentUserRole: AccountRole,
            pageInfo: PageInfo
    ): PageResult<User>

    fun deleteById(id: UserId)

    fun existsByPhoneNumber(phoneNumber: PhoneNumber): Boolean
    fun findAppliedConnectionsByRetailerIds(
            retailerIds: Set<UserId>,
            status: List<ApplyStatus>?,
            pageInfo: PageInfo
    ): Page<BizConnection>

    fun findReceivedConnectionsByWholesalerIds(
            wholesalerIds: Set<UserId>,
            status: List<ApplyStatus>?,
            pageInfo: PageInfo
    ): Page<BizConnection>
    fun findEmployeesByEmployerIds(employerIds: Set<UserId>): List<User>
    fun findEmployersByEmployeeIds(employeeIds: Set<UserId>): List<User>
}
