package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.repository

import com.querydsl.core.QueryFactory
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.BizConnectionJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.QBizConnectionJpo
import javax.persistence.EntityManager
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.QUserJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.QWholesalerConnectionJpo
import kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity.UserJpo
import kr.co.marketbill.marketbillcoreserver.user.domain.model.BizConnection
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.PhoneNumber
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepositoryImpl(private val jpaQuery: JPAQueryFactory) : UserQueryRepository {
    private val userJpo = QUserJpo.userJpo
    private val bizConnectionJpo = QBizConnectionJpo.bizConnectionJpo
    private val wholesalerConnectionJpo = QWholesalerConnectionJpo.wholesalerConnectionJpo

    override fun findUsersWithDynamicQuery(
        roles: List<AccountRole>?,
        name: String?,
        phoneNo: PhoneNumber?,
        pageable: Pageable
    ): Page<User> {
        val query =
            jpaQuery
                .selectFrom(userJpo)
                .where(rolesIn(roles?.map { it.name }), nameLike(name), phoneNoEq(phoneNo?.value))

        return toPageJpoResponse(query, pageable).map { User.fromJpo(it) }
    }

    override fun findConnectableUsers(
        currentUserId: UserId,
        currentUserRole: AccountRole,
        pageable: Pageable
    ): Page<User> {
        val query =
            jpaQuery
                .selectFrom(userJpo)
                .where(
                    oppositeRole(currentUserRole.name)
                        ?.and(excludeId(currentUserId.value))
                )

        return toPageJpoResponse(query, pageable).map { User.fromJpo(it) }
    }

    // 내가 소매상일 때: 내가 보낸 요청만 조회
    override fun findAppliedConnectionsByRetailerIds(
        retailerIds: Set<UserId>,
        status: List<ApplyStatus>?,
        pageable: Pageable
    ): Page<BizConnection> {

        val query = jpaQuery
            .selectFrom(bizConnectionJpo)
            .where(retailerIdsInAndApplyStatusIn(retailerIds, status))

        return toPageJpoResponse<BizConnectionJpo>(query, pageable)
            .map { BizConnection.fromJpo(it) }
    }


    // 내가 도매상일 때: 내가 받은 것만 조회
    override fun findReceivedConnectionsByWholesalerIds(
        wholesalerIds: Set<UserId>,
        status: List<ApplyStatus>?,
        pageable: Pageable
    ): Page<BizConnection> {
        val query = jpaQuery
            .selectFrom(bizConnectionJpo)
            .where(wholesalerIdsInAndApplyStatusIn(wholesalerIds, status))

        return toPageJpoResponse(query, pageable)
            .map { BizConnection.fromJpo(it) }

    }

    // userIds 도매상 사장님 기준으로 직원 목록을 가져옴
    override fun findEmployeesByEmployerIds(employerIds: Set<UserId>): List<User> {
        return jpaQuery
            .select(userJpo)
            .from(userJpo)
            .join(wholesalerConnectionJpo)
            .on(wholesalerConnectionJpo.employee.id.eq(userJpo.id))
            .where(wholesalerConnectionEmployerIdsIn(employerIds.map { it.value }.toSet()))
            .distinct()
            .fetch()
            .map { User.fromJpo(it) }
    }

    override fun findEmployersByEmployeeIds(employeeIds: Set<UserId>): List<User> {
        return jpaQuery
            .select(userJpo)
            .from(userJpo)
            .join(wholesalerConnectionJpo)
            .on(wholesalerConnectionJpo.employer.id.eq(userJpo.id))
            .where(wholesalerConnectionEmployeeIdsIn(employeeIds.map { it.value }.toSet()))
            .distinct()
            .fetch()
            .map { User.fromJpo(it) }
    }

    private fun <T> toPageJpoResponse(query: JPAQuery<T>, pageable: Pageable): Page<T> {
        val total = query.fetchCount()
        val content = query.offset(pageable.offset).limit(pageable.pageSize.toLong()).fetch()
        return PageImpl<T>(content, pageable, total)
    }

    private fun retailerIdsInAndApplyStatusIn(
        retailerIds: Set<UserId>?,
        status: List<ApplyStatus>?
    ): BooleanExpression? {
        if (retailerIds == null || status == null) return null;
        return bizConnectionJpo.retailer.id.`in`(retailerIds.map { it.value })
            .and(bizConnectionJpo.applyStatus.`in`(status))
    }

    private fun wholesalerIdsInAndApplyStatusIn(
        wholesalerIds: Set<UserId>?,
        status: List<ApplyStatus>?
    ): BooleanExpression? {
        if (wholesalerIds == null || status == null) return null;
        return bizConnectionJpo.wholesaler.id.`in`(wholesalerIds.map { it.value })
            .and(bizConnectionJpo.applyStatus.`in`(status))
    }

    private fun wholesalerConnectionEmployeeIdsIn(employeeIds: Set<Long>?): BooleanExpression? {
        if (employeeIds == null) return null
        return wholesalerConnectionJpo.employee.id.`in`(employeeIds)
    }

    private fun wholesalerConnectionEmployerIdsIn(employerIds: Set<Long>?): BooleanExpression? {
        if (employerIds == null) return null
        return wholesalerConnectionJpo.employer.id.`in`(employerIds)
    }

    private fun rolesIn(roles: List<String>?): BooleanExpression? {
        return if (!roles.isNullOrEmpty()) {
            userJpo.userCredentialJpo.role.stringValue().`in`(roles)
        } else null

    }

    private fun oppositeRole(role: String?): BooleanExpression? {
        if (role == null) return null
        return if (role == AccountRole.RETAILER.name) {
            rolesIn(listOf(AccountRole.WHOLESALER_EMPR.name, AccountRole.WHOLESALER_EMPE.name))
        } else {
            rolesIn(listOf(AccountRole.RETAILER.name))
        }
    }

    private fun excludeId(id: Long?): BooleanExpression? {
        if (id == null) return null
        return userJpo.id.ne(id)
    }

    private fun nameLike(name: String?): BooleanExpression? {
        return if (!name.isNullOrBlank()) {
            userJpo.name.containsIgnoreCase(name)
        } else null
    }

    private fun phoneNoEq(phoneNo: String?): BooleanExpression? {
        return if (!phoneNo.isNullOrBlank()) {
            userJpo.userCredentialJpo.phoneNo.eq(phoneNo)
        } else null
    }
}
