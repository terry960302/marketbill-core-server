package kr.co.marketbill.marketbillcoreserver.user.application.usecase

import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import kr.co.marketbill.marketbillcoreserver.user.application.command.FindConnectionsCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.FindUsersCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.UserSearchCommand
import kr.co.marketbill.marketbillcoreserver.user.application.port.outbound.UserRepository
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserSearchResult
import kr.co.marketbill.marketbillcoreserver.user.domain.model.BizConnection
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.stereotype.Component

@Component
class UserSearchUseCase(private val userRepository: UserRepository) {
    fun execute(command: UserSearchCommand): UserSearchResult {
        val pageResult = userRepository.search(command.criteria, command.pageInfo)

        val userResults = pageResult.content.map { user -> UserResult.from(user) }

        return UserSearchResult.from(
            users = userResults,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            currentPage = pageResult.pageInfo.page,
            hasNext = pageResult.hasNext
        )
    }

    fun getUserById(id: UserId): UserResult {
        val user = userRepository.findById(id) ?: throw MarketbillException(ErrorCode.NO_USER)
        return UserResult.from(user)
    }

    fun getConnectableUsers(
        currentUserId: UserId,
        currentUserRole: AccountRole,
        pageInfo: kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
    ): UserSearchResult {
        val pageResult =
            userRepository.findConnectableUsers(currentUserId, currentUserRole, pageInfo)
        val userResults = pageResult.content.map { user -> UserResult.from(user) }
        return UserSearchResult.from(
            users = userResults,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            currentPage = pageResult.pageInfo.page,
            hasNext = pageResult.hasNext
        )
    }

    fun findAppliedConnectionsByRetailerIds(command: FindConnectionsCommand): Map<UserId, List<BizConnection>> {
        return userRepository.findAppliedConnectionsByRetailerIds(
            command.userIds.map { UserId.from(it) }.toSet(),
            command.status,
            command.pageInfo
        )
            .groupBy { it.retailer!!.id!! }
    }

    fun findReceivedConnectionsByWholesalerIds(command: FindConnectionsCommand): Map<UserId, List<BizConnection>> {
        return userRepository.findReceivedConnectionsByWholesalerIds(
            command.userIds.map { UserId.from(it) }.toSet(),
            command.status,
            command.pageInfo
        )
            .groupBy { it.wholesaler!!.id!! }
    }

    fun findConnectedEmployeesByEmployerIds(command: FindUsersCommand): Map<UserId, List<User>> {
        return userRepository.findEmployeesByEmployerIds(command.userIds.map { UserId.from(it) }.toSet())
            .groupBy { it.id!! }
    }

    fun findConnectedEmployersByEmployeeIds(command: FindUsersCommand): Map<UserId, List<User>> {
        return userRepository.findEmployersByEmployeeIds(command.userIds.map { UserId.from(it) }.toSet())
            .groupBy { it.id!! }
    }

}
