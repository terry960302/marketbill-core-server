package kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kr.co.marketbill.marketbillcoreserver.user.application.command.FindConnectionsCommand
import kr.co.marketbill.marketbillcoreserver.user.application.command.FindUsersCommand
import kr.co.marketbill.marketbillcoreserver.user.application.service.UserService
import kr.co.marketbill.marketbillcoreserver.user.domain.model.User
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.dataloader.MappedBatchLoaderWithContext

@DgsDataLoader(name = "connectedEmployees")
class ConnectedEmployeesLoader(private val userService: UserService) :
    MappedBatchLoaderWithContext<Long, List<User>> {

    override fun load(
        keys: Set<Long>,
        environment: org.dataloader.BatchLoaderEnvironment
    ): CompletionStage<Map<Long, List<User>>> {
        val command = FindUsersCommand.from(keys)
        val results = userService.findConnectedEmployeesByUserIds(command)
        return CompletableFuture.completedFuture(results.mapKeys { it.key.value })
    }
}
