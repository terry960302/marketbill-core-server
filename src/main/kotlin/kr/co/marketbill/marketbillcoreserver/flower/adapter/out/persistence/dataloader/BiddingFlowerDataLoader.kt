package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.dataloader

import com.netflix.graphql.dgs.DgsDataLoader
import kr.co.marketbill.marketbillcoreserver.flower.application.command.BiddingFlowerSearchCommand
import kr.co.marketbill.marketbillcoreserver.flower.application.result.BiddingFlowerResult
import kr.co.marketbill.marketbillcoreserver.flower.application.service.FlowerService
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerId
import org.dataloader.MappedBatchLoaderWithContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "biddingFlowers")
class BiddingFlowerDataLoader(
    private val flowerService: FlowerService,
) : MappedBatchLoaderWithContext<Long, List<BiddingFlowerResult>> {

    override fun load(
        keys: Set<Long>,
        environment: org.dataloader.BatchLoaderEnvironment
    ): CompletionStage<Map<Long, List<BiddingFlowerResult>>> {
        val biddingFlowersMap: MutableMap<FlowerId, List<BiddingFlower>> =
            flowerService.findBiddingFlowersByFlowerIds(BiddingFlowerSearchCommand(keys))

        return CompletableFuture.completedFuture(
            biddingFlowersMap
                .mapKeys { it.key.value }
                .mapValues { (_, biddingFlowers) -> biddingFlowers.map { BiddingFlowerResult.from(it) } }
        )
    }
}