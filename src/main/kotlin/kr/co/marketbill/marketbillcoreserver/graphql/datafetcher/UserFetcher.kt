package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuthTokenDto
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
import org.springframework.web.bind.annotation.RequestHeader
import java.util.*
import java.util.concurrent.CompletableFuture

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
        return userService.getUser(userId)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetUsers)
    fun getUsers(
        dfe: DgsDataFetchingEnvironment,
        @RequestHeader("Authorization") authorization: String?,
        @InputArgument filter: UserFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<User> {
        var userId: Long? = null
        var role: kr.co.marketbill.marketbillcoreserver.constants.AccountRole? = null
        var roles: List<kr.co.marketbill.marketbillcoreserver.constants.AccountRole>? = null
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        if (authorization != null) {
            val token = jwtProvider.filterOnlyToken(authorization)
            userId = jwtProvider.parseUserId(token)
            role = jwtProvider.parseUserRole(token)
        }
        if (filter != null) {
            roles =
                filter.roles!!.map { kr.co.marketbill.marketbillcoreserver.constants.AccountRole.valueOf(it.toString()) }
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
    fun signUp(@InputArgument input: SignUpInput): AuthTokenDto {
        return userService.signUp(input)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SignIn)
    fun signIn(@InputArgument input: SignInInput): AuthTokenDto {
        return userService.signIn(input)
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.ApplyBizConnection)
    fun applyBizConnection(
        @RequestHeader("Authorization") authorization: String,
        @InputArgument wholesalerId: Long
    ): BizConnection {
        val token = jwtProvider.filterOnlyToken(authorization)
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
}