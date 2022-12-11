package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.AppliedConnectionLoader
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.ReceivedConnectionLoader
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.service.TokenService
import kr.co.marketbill.marketbillcoreserver.service.UserService
import kr.co.marketbill.marketbillcoreserver.types.*
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.context.request.ServletWebRequest
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletResponse

@DgsComponent
class UserFetcher {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @PreAuthorize("isAuthenticated")
    @DgsQuery(field = DgsConstants.QUERY.Me)
    fun me(
        @RequestHeader(
            value = JwtProvider.AUTHORIZATION_HEADER_NAME,
            required = true
        ) authorization: String
    ): Optional<User> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        return userService.getUser(userId)
    }

    @DgsQuery(field = DgsConstants.QUERY.GetUsers)
    fun getUsers(
        dfe: DgsDataFetchingEnvironment,
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = false) authorization: Optional<String>,
        @InputArgument filter: UserFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<User> {
        var userId: Long? = null
        var role: AccountRole? = null
        var roles: List<AccountRole>? = null
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        if (authorization.isPresent) {
            val token = jwtProvider.filterOnlyToken(authorization.get())
            userId = jwtProvider.parseUserId(token)
            role = jwtProvider.parseUserRole(token)
        }
        if (filter != null) {
            roles =
                filter.roles.map { kr.co.marketbill.marketbillcoreserver.constants.AccountRole.valueOf(it.toString()) }
        }

        val selection = dfe.selectionSet
        val needFetchApplyStatus = selection.contains("applyStatus") || selection.contains("bizConnectionId")

        return if (needFetchApplyStatus) {
            userService.getUsersWithApplyStatus(userId, role, pageable)
        } else {
            userService.getAllUsers(roles, pageable)
        }
    }

    @DgsMutation(field = DgsConstants.MUTATION.RemoveUser)
    fun removeUser(@InputArgument userId: Long): CommonResponse {
        try {
            userService.deleteUser(userId)
            return CommonResponse(success = true)
        } catch (e: Exception) {
            throw CustomException(message = e.message!!)
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
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
        dfe: DgsDataFetchingEnvironment
    ): CommonResponse {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        userService.signOut(userId)
        return CommonResponse(
            success = true
        )
    }


    @DgsMutation(field = DgsConstants.MUTATION.ReissueToken)
    fun reissueToken(
        @RequestHeader(
            value = JwtProvider.AUTHORIZATION_HEADER_NAME,
            required = true
        ) authorization: String
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
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
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
        @InputArgument status: kr.co.marketbill.marketbillcoreserver.types.ApplyStatus
    ): BizConnection {
        return userService.updateBizConnection(bizConnId, ApplyStatus.valueOf(status.toString()))
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = DgsConstants.USER.AppliedConnections)
    fun appliedConnections(
        dfe: DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?,
        @InputArgument filter: BizConnectionFilterInput?
    ): CompletableFuture<List<BizConnection>> {
        val user = dfe.getSource<User>()
        val context = DgsContext.getCustomContext<CustomContext>(dfe)
        val dataLoader = dfe.getDataLoader<Long, List<BizConnection>>(AppliedConnectionLoader::class.java)

        context.appliedConnectionsInput.pagination = pagination
        context.appliedConnectionsInput.filter = filter

        return dataLoader.load(user.id)
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = DgsConstants.USER.ReceivedConnections)
    fun receivedConnections(
        dfe: DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?,
        @InputArgument filter: BizConnectionFilterInput?
    ): CompletableFuture<List<BizConnection>> {
        val user = dfe.getSource<User>()
        val context = DgsContext.getCustomContext<CustomContext>(dfe)
        val dataLoader = dfe.getDataLoader<Long, List<BizConnection>>(ReceivedConnectionLoader::class.java)

        context.receivedConnectionsInput.pagination = pagination
        context.receivedConnectionsInput.filter = filter

        return dataLoader.load(user.id)
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = DgsConstants.USER.ConnectedEmployer)
    fun connectedEmployer(dfe: DgsDataFetchingEnvironment): Optional<User> {
        val user = dfe.getSource<User>()
        user.mapConnectedEmployer()
        return Optional.ofNullable(user.connectedEmployer)
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = DgsConstants.USER.ConnectedEmployees)
    fun connectedEmployees(dfe: DgsDataFetchingEnvironment): List<User> {
        val user = dfe.getSource<User>()
        user.mapConnectedEmployees()
        return user.connectedEmployees
    }

    @Deprecated(message = "This function for cookie jwt method.")
    fun getHttpServletResponseFromDfe(dfe: DgsDataFetchingEnvironment): HttpServletResponse {
        val requestData: DgsWebMvcRequestData = dfe.getDgsContext().requestData as DgsWebMvcRequestData
        val webRequest: ServletWebRequest = requestData.webRequest as ServletWebRequest
        return webRequest.response!!
    }
}