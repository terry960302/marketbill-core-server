package kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.context.BizConnectionContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kr.co.marketbill.marketbillcoreserver.user.application.command.FindConnectionsCommand
import kr.co.marketbill.marketbillcoreserver.user.application.service.UserService
import kr.co.marketbill.marketbillcoreserver.user.domain.model.BizConnection
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.dataloader.MappedBatchLoaderWithContext

@DgsDataLoader(name = "receivedConnections")
class ReceivedConnectionLoader(
    private val userService: UserService
) : MappedBatchLoaderWithContext<Long, List<BizConnection>> {

    override fun load(
        keys: Set<Long>,
        environment: org.dataloader.BatchLoaderEnvironment
    ): CompletionStage<Map<Long, List<BizConnection>>> {
        val context = environment.keyContexts as BizConnectionContext
        val command = FindConnectionsCommand.from(keys, context.status, context.pageInfo)
        val results = userService.findReceivedConnectionsByWholesalerIds(command)
        return CompletableFuture.completedFuture(results.mapKeys { it.key.value })
    }
}
