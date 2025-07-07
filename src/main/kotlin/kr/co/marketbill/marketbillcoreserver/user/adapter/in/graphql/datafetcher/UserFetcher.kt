package kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.shared.domain.model.CustomUserDetails
import kr.co.marketbill.marketbillcoreserver.types.*
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.mapper.UserOutputMapper
import kr.co.marketbill.marketbillcoreserver.user.application.command.DeleteUserCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.GetUserCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.MeCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.SignInCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.SignUpCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.UserSearchCommand
import kr.co.marketbill.marketbillcoreserver.user.application.service.UserService
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal

@DgsComponent
class UserFetcher(
    private val userService: UserService,
    private val mapper: UserOutputMapper,
) {

    @DgsQuery(field = DgsConstants.QUERY.Me)
    @PreAuthorize("isAuthenticated")
    fun me(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): User {
        val command = MeCommand.fromUserId(userDetails.getUserId().value)
        val result = userService.me(command)
        return mapper.toUser(result)
    }

    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY_TYPE, field = DgsConstants.QUERY.User)
    )
    fun getUser(@InputArgument id: Int): User {
        val command = GetUserCommand.fromGraphql(id)
        val result = userService.getUser(command)
        return mapper.toUser(result)
    }

    /**
     * 사용자 목록 조회
     * - 단순 조회
     * - connectable : 로그인한 경우, 내가 연결이 가능한 사용자 목록 조회
     */
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY_TYPE, field = DgsConstants.QUERY.Users)
    )
    fun getUsers(
        @InputArgument filter: UserFilterInput?,
        @InputArgument pagination: PaginationInput?,
        @AuthenticationPrincipal userDetails: CustomUserDetails?
    ): List<User> {
        val command =
            UserSearchCommand.fromGraphql(
                currentUserId = userDetails?.getUserId()?.value,
                currentUserRole = userDetails?.getRole(),
                roles = filter?.roles?.map { AccountRole.valueOf(it.toString()) },
                phoneNumber = filter?.phoneNo,
                name = filter?.name,
                connectable = filter?.connectable,
                page = pagination?.page ?: 0,
                size = pagination?.size ?: 20
            )

        val result = userService.search(command)
        return mapper.toUsers(result)
    }

    @DgsMutation(field = DgsConstants.MUTATION.SignUp)
    fun signUp(@InputArgument input: SignUpInput): AuthToken {
        val command =
            SignUpCommand.fromGraphql(
                name = input.name,
                phoneNumber = input.phoneNo,
                password = input.password,
                role = AccountRole.valueOf(input.role.toString()),
            )

        val result = userService.signUp(command)
        return mapper.toAuthToken(result)
    }

    @DgsMutation(field = DgsConstants.MUTATION.SignIn)
    fun signIn(@InputArgument input: SignInInput): AuthToken {
        val command =
            SignInCommand.fromGraphql(
                phoneNumber = input.phoneNo,
                password = input.password,
            )

        val result = userService.signIn(command)
        return mapper.toAuthToken(result)
    }

    @PreAuthorize("isAuthenticated")
    @DgsMutation(field = DgsConstants.MUTATION.RemoveUser)
    fun removeUser(@InputArgument userId: Int): CommonResponse {
        val command = DeleteUserCommand.fromGraphql(userId)
        userService.deleteUser(command)
        return CommonResponse(success = true)
    }
}
