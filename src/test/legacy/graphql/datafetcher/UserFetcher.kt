package kr.co.marketbill.marketbillcoreserver.legacy.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import java.util.*
import javax.servlet.http.HttpServletResponse
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.application.service.user.TokenService
import kr.co.marketbill.marketbillcoreserver.application.service.user.UserService
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.infrastructure.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.shared.exception.InternalErrorException
import kr.co.marketbill.marketbillcoreserver.shared.util.GqlDtoConverter
import kr.co.marketbill.marketbillcoreserver.types.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.context.request.ServletWebRequest

@DgsComponent
class UserFetcher {

    @Autowired private lateinit var userService: UserService

    @Autowired private lateinit var tokenService: TokenService

    @Autowired private lateinit var jwtProvider: JwtProvider

    @PreAuthorize("isAuthenticated")
    @DgsQuery(field = DgsConstants.QUERY.Me)
    fun me(
            @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true)
            authorization: String
    ): User {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        return userService.getUser(userId)
    }

    @DgsData.List(
            DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetUser),
            DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.User),
    )
    fun getUser(@InputArgument id: Long): User {
        return userService.getUser(id)
    }

    @DgsData.List(
            DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetUsers),
            DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.Users),
    )
    fun getUsers(
        dfe: DgsDataFetchingEnvironment,
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = false)
            authorization: Optional<String>,
        @InputArgument filter: UserFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<User> {
        var userId: Long? = null
        var role: AccountRole? = null
        var roles: List<AccountRole>? = null
        var phoneNo: String? = null
        var name: String? = null
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        authorization.ifPresent {
            val token = jwtProvider.filterOnlyToken(it)
            userId = jwtProvider.parseUserId(token)
            role = jwtProvider.parseUserRole(token)
        }
        if (filter != null) {
            roles =
                    filter.roles?.map {
                        AccountRole.valueOf(
                                it.toString()
                        )
                    }
            phoneNo = filter.phoneNo
            name = filter.name
        }

        val selection = dfe.selectionSet
        val needFetchApplyStatus =
                selection.contains("applyStatus") || selection.contains("bizConnectionId")

        return if (needFetchApplyStatus) {
            userService.getUsersWithApplyStatus(userId, role, pageable)
        } else {
            userService.getUsers(roles, phoneNo, name, pageable)
        }
    }

    @DgsData.List(
            DgsData(
                    parentType = DgsConstants.QUERY.TYPE_NAME,
                    field = DgsConstants.QUERY.GetConnectableUsers
            ),
            DgsData(
                    parentType = DgsConstants.QUERY.TYPE_NAME,
                    field = DgsConstants.QUERY.ConnectableUsers
            ),
    )
    fun getConnectableUsers(
            dfe: DgsDataFetchingEnvironment,
            @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true)
            authorization: String,
            @InputArgument pagination: PaginationInput?
    ): Page<User> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        val role = jwtProvider.parseUserRole(token)
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        return userService.getUsersWithApplyStatus(userId, role, pageable)
    }

    @DgsMutation(field = DgsConstants.MUTATION.RemoveUser)
    fun removeUser(@InputArgument userId: Long): CommonResponse {
        try {
            userService.removeUser(userId)
            return CommonResponse(success = true)
        } catch (e: Exception) {
            throw InternalErrorException(message = e.message!!)
        }
    }

    @DgsMutation(field = DgsConstants.MUTATION.SignUp)
    fun signUp(@InputArgument input: SignUpInput): AuthToken {
        val newToken = userService.signUp(input)
        return AuthToken(accessToken = newToken.accessToken, refreshToken = newToken.refreshToken)
    }

    @DgsMutation(field = DgsConstants.MUTATION.SignIn)
    fun signIn(@InputArgument input: SignInInput): AuthToken {
        val newToken = userService.signIn(input)
        return AuthToken(accessToken = newToken.accessToken, refreshToken = newToken.refreshToken)
    }

    @DgsMutation(field = DgsConstants.MUTATION.SignOut)
    fun signOut(
            @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true)
            authorization: String,
            dfe: DgsDataFetchingEnvironment
    ): CommonResponse {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        userService.signOut(userId)
        return CommonResponse(success = true)
    }

    @DgsMutation(field = DgsConstants.MUTATION.ReissueToken)
    fun reissueToken(
            @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true)
            authorization: String
    ): AuthToken {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        val role = jwtProvider.parseUserRole(token)
        val newToken = tokenService.reissueToken(userId, role)
        return AuthToken(accessToken = newToken.accessToken, refreshToken = newToken.refreshToken)
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsMutation(field = DgsConstants.MUTATION.ApplyBizConnection)
    fun applyBizConnection(
            @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true)
            authorization: String,
            @InputArgument wholesalerId: Long
    ): BizConnection {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        return userService.createBizConnection(userId, wholesalerId)
    }

    @PreAuthorize("hasRole('WHOLESALER_EMPR') or hasRole('WHOLESALER_EMPE')")
    @DgsMutation(field = DgsConstants.MUTATION.UpdateBizConnection)
    fun updateBizConnection(
            @InputArgument bizConnId: Long,
            @InputArgument status: ApplyStatus
    ): BizConnection {
        return userService.updateBizConnection(bizConnId, kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus.valueOf(status.toString()))
    }

    @DgsMutation(field = DgsConstants.MUTATION.UpsertBusinessInfo)
    fun upsertBusinessInfo(
            @InputArgument input: CreateBusinessInfoInput
    ): kr.co.marketbill.marketbillcoreserver.domain.entity.user.BusinessInfo {
        return userService.upsertBusinessInfo(input)
    }

    @DgsMutation(field = DgsConstants.MUTATION.UpdatePassword)
    fun updatePassword(@InputArgument input: UpdatePasswordInput): CommonResponse {
        try {
            userService.updatePassword(input)
            return CommonResponse(success = true)
        } catch (e: Exception) {
            throw e
        }
    }

    @Deprecated(message = "This function for cookie jwt method.")
    fun getHttpServletResponseFromDfe(dfe: DgsDataFetchingEnvironment): HttpServletResponse {
        val requestData: DgsWebMvcRequestData =
                dfe.getDgsContext().requestData as DgsWebMvcRequestData
        val webRequest: ServletWebRequest = requestData.webRequest as ServletWebRequest
        return webRequest.response!!
    }
}
