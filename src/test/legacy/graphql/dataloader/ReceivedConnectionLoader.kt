package kr.co.marketbill.marketbillcoreserver.legacy.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.presentation.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.application.service.user.UserService
import kr.co.marketbill.marketbillcoreserver.shared.util.GqlDtoConverter
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired

@DgsDataLoader(name = "receivedConnections")
class ReceivedConnectionLoader : MappedBatchLoaderWithContext<Long, List<BizConnection>> {
    @Autowired private lateinit var userService: UserService

    override fun load(
            keys: MutableSet<Long>?,
            env: BatchLoaderEnvironment
    ): CompletionStage<MutableMap<Long, List<BizConnection>>> {

        var applyStatus: List<ApplyStatus>? = null

        val context = DgsContext.Companion.getCustomContext<CustomContext>(env)
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
