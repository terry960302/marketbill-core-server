package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import io.netty.handler.codec.http.cookie.Cookie
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.AppliedConnectionLoader
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.ReceivedConnectionLoader
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.service.UserService
import kr.co.marketbill.marketbillcoreserver.types.*
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CookieValue
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
    private lateinit var jwtProvider: JwtProvider

    @PreAuthorize("isAuthenticated")
    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.Me)
    fun me(@CookieValue(value = JwtProvider.ACCESS_TOKEN_COOKIE_NAME, required = true) token: String): Optional<User> {
        val userId = jwtProvider.parseUserId(token)
        return userService.getUser(userId)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetUsers)
    fun getUsers(
        dfe: DgsDataFetchingEnvironment,
        @CookieValue(value = JwtProvider.ACCESS_TOKEN_COOKIE_NAME, required = false) token: Optional<String>,
        @InputArgument filter: UserFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<User> {
        var userId: Long? = null
        var role: AccountRole? = null
        var roles: List<AccountRole>? = null
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        if (token.isPresent) {
            userId = jwtProvider.parseUserId(token.get())
            role = jwtProvider.parseUserRole(token.get())
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


    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SignUp)
    fun signUp(@InputArgument input: SignUpInput, dfe: DgsDataFetchingEnvironment): AuthToken {
        val newToken = userService.signUp(input)
        val response = getHttpServletResponseFromDfe(dfe)
        jwtProvider.setAllTokensToHttpOnlyCookie(response, newToken)
        return AuthToken(accessToken = newToken.accessToken)

    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SignIn)
    fun signIn(@InputArgument input: SignInInput, dfe: DgsDataFetchingEnvironment): AuthToken {
        val newToken = userService.signIn(input)
        val response = getHttpServletResponseFromDfe(dfe)
        jwtProvider.setAllTokensToHttpOnlyCookie(response, newToken)
        return AuthToken(accessToken = newToken.accessToken)
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.ApplyBizConnection)
    fun applyBizConnection(
        @CookieValue(value = JwtProvider.ACCESS_TOKEN_COOKIE_NAME, required = true) token: String,
        @InputArgument wholesalerId: Long
    ): BizConnection {
        val userId = jwtProvider.parseUserId(token)
        return userService.createBizConnection(userId, wholesalerId)
    }

    @PreAuthorize("hasRole('WHOLESALER_EMPR') or hasRole('WHOLESALER_EMPE')")
    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.UpdateBizConnection)
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

    fun getHttpServletResponseFromDfe(dfe: DgsDataFetchingEnvironment): HttpServletResponse {
        val requestData: DgsWebMvcRequestData = dfe.getDgsContext().requestData as DgsWebMvcRequestData
        val webRequest: ServletWebRequest = requestData.webRequest as ServletWebRequest
        return webRequest.response!!
    }
}