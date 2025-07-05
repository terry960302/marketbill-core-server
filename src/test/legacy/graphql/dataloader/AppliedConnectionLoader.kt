package kr.co.marketbill.marketbillcoreserver.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.presentation.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.application.service.user.UserService
import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.shared.util.GqlDtoConverter
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

@DgsDataLoader(name = "appliedConnections")
class AppliedConnectionLoader(private val userService: UserService) :
    MappedBatchLoaderWithContext<Long, List<BizConnection>> {


    @Qualifier
    override fun load(
        keys: MutableSet<Long>?,
        env: BatchLoaderEnvironment
    ): CompletionStage<Map<Long, List<BizConnection>>> {

        var applyStatus: List<ApplyStatus>? = null
        val context = DgsContext.getCustomContext<CustomContext>(env)
        val pagination = context.appliedConnectionsInput.pagination
        val filter = context.appliedConnectionsInput.filter

        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)
        if (filter != null) {
            applyStatus = filter.applyStatus.map { ApplyStatus.valueOf(it.toString()) }
        }

        return if (keys == null) {
            CompletableFuture.completedFuture(emptyMap<Long, List<BizConnection>>().toMutableMap())
        } else {
            CompletableFuture.supplyAsync {
                userService.getAppliedConnectionsByRetailerIds(
                    keys.stream().toList(),
                    applyStatus,
                    pageable
                )
            }
        }
    }
}
