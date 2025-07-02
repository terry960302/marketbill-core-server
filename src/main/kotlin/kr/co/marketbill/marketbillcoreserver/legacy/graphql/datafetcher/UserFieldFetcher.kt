package kr.co.marketbill.marketbillcoreserver.legacy.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.AppliedConnectionLoader
import kr.co.marketbill.marketbillcoreserver.presentation.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.presentation.graphql.dataloader.ReceivedConnectionLoader
import kr.co.marketbill.marketbillcoreserver.types.BizConnectionFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import java.util.*
import java.util.concurrent.CompletableFuture

@DgsComponent
class UserFieldFetcher {
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
}