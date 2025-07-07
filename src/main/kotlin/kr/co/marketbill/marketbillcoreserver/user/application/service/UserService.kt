package kr.co.marketbill.marketbillcoreserver.user.application.service

import kr.co.marketbill.marketbillcoreserver.user.application.command.DeleteUserCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.FindConnectionsCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.FindUsersCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.GetUserCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.MeCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.SignInCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.SignUpCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.UserSearchCommand
import kr.co.marketbill.marketbillcoreserver.user.application.result.AuthTokenResult
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserSearchResult
import kr.co.marketbill.marketbillcoreserver.user.application.usecase.UserSearchUseCase
import kr.co.marketbill.marketbillcoreserver.user.application.usecase.UserSignInUseCase
import kr.co.marketbill.marketbillcoreserver.user.application.usecase.UserSignUpUseCase
import kr.co.marketbill.marketbillcoreserver.user.domain.model.BizConnection
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userSignUpUseCase: UserSignUpUseCase,
    private val userSignInUseCase: UserSignInUseCase,
    private val userSearchUseCase: UserSearchUseCase
) {
    fun signUp(command: SignUpCommand): AuthTokenResult {
        return userSignUpUseCase.execute(command)
    }

    fun signIn(command: SignInCommand): AuthTokenResult {
        return userSignInUseCase.execute(command)
    }

    fun search(command: UserSearchCommand): UserSearchResult {
        if (command.canSearchConnectable()) {
            return userSearchUseCase.getConnectableUsers(
                UserId.from(command.criteria.currentUserId!!),
                command.criteria.currentUserRole!!,
                command.pageInfo
            )
        }
        return userSearchUseCase.execute(command)
    }

    fun getUser(command: GetUserCommand): UserResult {
        return userSearchUseCase.getUserById(UserId.from(command.userId))
    }

    fun me(command: MeCommand): UserResult {
        return userSearchUseCase.getUserById(UserId.from(command.userId))
    }

    fun deleteUser(command: DeleteUserCommand) {
        userSignUpUseCase.deleteUser(UserId.from(command.userId))
    }

    // DataLoader에서 사용하는 메서드들
    fun findAppliedConnectionsByRetailerIds(
        command: FindConnectionsCommand
    ): Map<UserId, List<BizConnection>> {
        return userSearchUseCase.findAppliedConnectionsByRetailerIds(command)
    }

    fun findReceivedConnectionsByWholesalerIds(
        command: FindConnectionsCommand
    ): Map<UserId, List<BizConnection>> {
        return userSearchUseCase.findReceivedConnectionsByWholesalerIds(command)
    }

    fun findConnectedEmployersByUserIds(command: FindUsersCommand): Map<UserId, List<User>?> {
        return userSearchUseCase.findConnectedEmployersByEmployeeIds(command)
    }

    fun findConnectedEmployeesByUserIds(
        command: FindUsersCommand
    ): Map<UserId, List<User>> {
        return userSearchUseCase.findConnectedEmployeesByEmployerIds(command)
    }
}
