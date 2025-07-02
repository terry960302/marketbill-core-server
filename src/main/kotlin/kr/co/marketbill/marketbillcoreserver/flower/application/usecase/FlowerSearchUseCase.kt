package kr.co.marketbill.marketbillcoreserver.flower.application.usecase

import kr.co.marketbill.marketbillcoreserver.flower.application.command.FlowerSearchCommand
import kr.co.marketbill.marketbillcoreserver.flower.application.port.outbound.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerSearchResult
import org.springframework.stereotype.Component

@Component
class FlowerSearchUseCase(
    private val flowerRepository: FlowerRepository
) {
    fun execute(command: FlowerSearchCommand): FlowerSearchResult {
        val criteria = command.toCriteria()
        val pageInfo = command.toPageInfo()

        val flowers = flowerRepository.findFlowersWithCriteria(criteria, pageInfo)

        return FlowerSearchResult.from(flowers)
    }
}
