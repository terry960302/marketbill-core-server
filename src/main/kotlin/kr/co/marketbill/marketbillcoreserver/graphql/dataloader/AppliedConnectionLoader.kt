package kr.co.marketbill.marketbillcoreserver.graphql.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.graphql.context.CustomContext
import kr.co.marketbill.marketbillcoreserver.service.UserService
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.PageRequest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "appliedConnections")
class AppliedConnectionLoader : MappedBatchLoaderWithContext<Long, List<BizConnection>> {

    @Autowired
    private lateinit var userService: UserService

    @Qualifier
    override fun load(
        keys: MutableSet<Long>?,
        env: BatchLoaderEnvironment
    ): CompletionStage<MutableMap<Long, List<BizConnection>>> {

        var pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        var applyStatus: ApplyStatus? = null
        val context =
            DgsContext.getCustomContext<CustomContext>(env)
        val pagination = context.appliedConnectionsInput.pagination
        val filter = context.appliedConnectionsInput.filter

        if (pagination != null) {
            pageable = PageRequest.of(pagination.page!!, pagination.size!!)
        }
        if (filter != null) {
            applyStatus = ApplyStatus.valueOf(filter.applyStatus.toString())
        }

        return if(keys == null){
            CompletableFuture.completedFuture(emptyMap<Long, List<BizConnection>>().toMutableMap())
        }else{
            CompletableFuture.supplyAsync {
                userService.getAppliedConnectionsByRetailerIds(keys.stream().toList(), applyStatus, pageable)
            }
        }

    }
}