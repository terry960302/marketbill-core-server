package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.client.GraphQLError
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.service.UserService
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.types.SignInInput
import kr.co.marketbill.marketbillcoreserver.types.SignUpInput
import kr.co.marketbill.marketbillcoreserver.types.UserFilterInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.RequestHeader
import java.util.*

@DgsComponent
class UserFetcher {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.Me)
    fun me(@RequestHeader("Authorization") authorization: String): Optional<User> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        return userService.me(userId)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SignUp)
    fun signUp(@InputArgument input: SignUpInput): AuthTokenDto {
        return userService.signUp(input)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SignIn)
    fun signIn(@InputArgument input: SignInInput): AuthTokenDto {
        return userService.signIn(input)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetUsers)
    fun getUsers(
        @RequestHeader("Authorization") authorization: String?,
        @InputArgument filter: UserFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<User> {
        try {
            var userId: Long? = null
            var roles: List<AccountRole>? = null
            var pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)

            if (filter != null) {
                if (filter.excludeMe == true) {
                    if (authorization == null) throw Exception("'excludeMe' parameter needs authorization token")
                    val token = jwtProvider.filterOnlyToken(authorization)
                    userId = jwtProvider.parseUserId(token)
                }
                if (filter.roles != null) {
                    roles = filter.roles.map { AccountRole.valueOf(it.toString()) }
                }
            }

            if (pagination != null) {
                pageable = PageRequest.of(pagination.page!!, pagination.size!!)
            }

            return userService.getUsers(userId, roles, pageable)
        } catch (e: Exception) {
            throw e
        }
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.ApplyBizConnection)
    fun applyBizConnection(@InputArgument retailerId: Long, @InputArgument wholesalerId: Long): BizConnection {
        return userService.createBizConnection(retailerId, wholesalerId)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.UpdateBizConnection)
    fun updateBizConnection(
        @InputArgument bizConnId: Long,
        @InputArgument status: kr.co.marketbill.marketbillcoreserver.types.ApplyStatus
    ): BizConnection {
        return userService.updateBizConnection(bizConnId, ApplyStatus.valueOf(status.toString()))
    }
}