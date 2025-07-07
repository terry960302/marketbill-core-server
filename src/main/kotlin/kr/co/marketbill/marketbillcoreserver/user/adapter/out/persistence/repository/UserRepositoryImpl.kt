package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo
import kr.co.marketbill.marketbillcoreserver.user.application.command.UserSearchCriteria
import kr.co.marketbill.marketbillcoreserver.user.application.port.outbound.UserRepository
import kr.co.marketbill.marketbillcoreserver.user.domain.model.BizConnection
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.PhoneNumber
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
        private val userQueryRepository: UserQueryRepository,
        private val userCrudRepository: UserCrudRepository
) : UserRepository {

    override fun save(user: User): User {
        return User.fromJpo(userCrudRepository.save(User.toJpo(user)))
    }

    override fun findById(id: UserId): User? {
        val jpo = userCrudRepository.findById(id.value).orElse(null)
        return jpo?.let { User.fromJpo(it) }
    }

    override fun findByIds(ids: Set<UserId>): Map<UserId, User> {
        val userJpos = userCrudRepository.findAllById(ids.map { it.value })
        return userJpos.associate { userJpo ->
            val userId = UserId(userJpo.id!!)
            userId to User.fromJpo(userJpo)
        }
    }

    override fun findByPhoneNumber(phoneNumber: PhoneNumber): User? {
        val userJpo: UserJpo? = userCrudRepository.findByUserCredentialJpo_PhoneNo(phoneNumber.value)
        return userJpo?.let { User.fromJpo(userJpo) }
    }

    override fun search(criteria: UserSearchCriteria, pageInfo: PageInfo): PageResult<User> {
        val pageRequest = PageRequest.of(pageInfo.page, pageInfo.size)
        val users: Page<User> =
                userQueryRepository.findUsersWithDynamicQuery(
                        roles = criteria.roles,
                        name = criteria.name,
                        phoneNo =
                                criteria.phoneNumber?.let {
                                    PhoneNumber.from(criteria.phoneNumber)
                                },
                        pageable = pageRequest
                )

        return PageResult(
                content = users.content,
                pageInfo = pageInfo,
                totalElements = users.totalElements
        )
    }

    override fun findConnectableUsers(
            currentUserId: UserId,
            currentUserRole: AccountRole,
            pageInfo: PageInfo
    ): PageResult<User> {
        val pageable = PageRequest.of(pageInfo.page, pageInfo.size)
        val users =
                userQueryRepository.findConnectableUsers(currentUserId, currentUserRole, pageable)
        return PageResult(
                content = users.content,
                pageInfo = pageInfo,
                totalElements = users.totalElements
        )
    }

    override fun deleteById(id: UserId) {
        userCrudRepository.deleteById(id.value)
    }

    override fun existsByPhoneNumber(phoneNumber: PhoneNumber): Boolean {
        return userCrudRepository.existsByUserCredentialJpo_PhoneNo(phoneNo = phoneNumber.value)
    }

    override fun findAppliedConnectionsByRetailerIds(
            retailerIds: Set<UserId>,
            status: List<ApplyStatus>?,
            pageInfo: PageInfo
    ): Page<BizConnection> {
        val pageable = PageRequest.of(pageInfo.page, pageInfo.size)
        return userQueryRepository.findAppliedConnectionsByRetailerIds(
                retailerIds,
                status,
                pageable
        )
    }

    override fun findReceivedConnectionsByWholesalerIds(
            wholesalerIds: Set<UserId>,
            status: List<ApplyStatus>?,
            pageInfo: PageInfo
    ): Page<BizConnection> {
        val pageable = PageRequest.of(pageInfo.page, pageInfo.size)
        return userQueryRepository.findReceivedConnectionsByWholesalerIds(
                wholesalerIds,
                status,
                pageable
        )
    }

    override fun findEmployeesByEmployerIds(employerIds: Set<UserId>): List<User> {
        return userQueryRepository.findEmployeesByEmployerIds(employerIds)
    }

    override fun findEmployersByEmployeeIds(employeeIds: Set<UserId>): List<User> {
        return userQueryRepository.findEmployersByEmployeeIds(employeeIds)
    }
}
