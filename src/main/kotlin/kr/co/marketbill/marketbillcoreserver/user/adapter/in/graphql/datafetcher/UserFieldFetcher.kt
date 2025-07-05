package kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.mapper.toPageInfo
import kr.co.marketbill.marketbillcoreserver.types.BizConnectionFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.context.BizConnectionKeyContext
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.dataloader.AppliedConnectionLoader
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.dataloader.ConnectedEmployeesLoader
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.dataloader.ConnectedEmployerLoader
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.dataloader.ReceivedConnectionLoader
import kr.co.marketbill.marketbillcoreserver.user.domain.model.BizConnection
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
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
        val dataLoader = dfe.getDataLoader<Long, List<BizConnection>>(AppliedConnectionLoader::class.java)

        return dataLoader.load(
            user.id!!.value,
            BizConnectionKeyContext.from(filter?.applyStatus, pagination.toPageInfo())
        )
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = DgsConstants.USER.ReceivedConnections)
    fun receivedConnections(
        dfe: DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?,
        @InputArgument filter: BizConnectionFilterInput?
    ): CompletableFuture<List<BizConnection>> {
        val user = dfe.getSource<User>()
        val dataLoader = dfe.getDataLoader<Long, List<BizConnection>>(ReceivedConnectionLoader::class.java)

        return dataLoader.load(
            user.id!!.value,
            BizConnectionKeyContext.from(filter?.applyStatus, pagination.toPageInfo())
        )
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = DgsConstants.USER.ConnectedEmployer)
    fun connectedEmployer(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<User>> {
        val user = dfe.getSource<User>()
        val dataLoader = dfe.getDataLoader<Long, List<User>>(ConnectedEmployerLoader::class.java)

        return dataLoader.load(user.id!!.value)
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = DgsConstants.USER.ConnectedEmployees)
    fun connectedEmployees(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<User>> {
        val user = dfe.getSource<User>()
        val dataLoader = dfe.getDataLoader<Long, List<User>>(ConnectedEmployeesLoader::class.java)

        return dataLoader.load(user.id!!.value)
    }
}