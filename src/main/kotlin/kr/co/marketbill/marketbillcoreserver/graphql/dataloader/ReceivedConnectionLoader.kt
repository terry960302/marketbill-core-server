package kr.co.marketbill.marketbillcoreserver.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.service.UserService
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


@DgsDataLoader(name = "receivedConnections")
class ReceivedConnectionLoader : MappedBatchLoaderWithContext<Long, List<BizConnection>> {
    @Autowired
    private lateinit var userService: UserService

    override fun load(
        keys: MutableSet<Long>?,
        env: BatchLoaderEnvironment
    ): CompletionStage<MutableMap<Long, List<BizConnection>>> {

        var applyStatus: List<ApplyStatus>? = null

        val context =
            DgsContext.Companion.getCustomContext<CustomContext>(env)
        val pagination = context.receivedConnectionsInput.pagination
        val filter = context.receivedConnectionsInput.filter

        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)
        if (filter != null) {
            applyStatus = filter.applyStatus.map { ApplyStatus.valueOf(it.toString()) }
        }
        return CompletableFuture.supplyAsync {
            userService.getReceivedConnectionsByWholesalerIds(
                keys!!.stream().toList(),
                applyStatus,
                pageable
            )
        }
    }
}