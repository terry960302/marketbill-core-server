package kr.co.marketbill.marketbillcoreserver.flower.application.usecase

import kr.co.marketbill.marketbillcoreserver.flower.application.command.BiddingFlowerSearchCommand
import kr.co.marketbill.marketbillcoreserver.flower.application.command.FlowerSearchCommand
import kr.co.marketbill.marketbillcoreserver.flower.application.port.outbound.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerSearchResult
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerId
import org.springframework.stereotype.Component

@Component
class BiddingFlowerSearchUseCase(
    private val flowerRepository: FlowerRepository
) {
    fun execute(command: BiddingFlowerSearchCommand): List<BiddingFlower> {
        return flowerRepository.findBiddingFlowersByFlowerIds(command.ids.map { FlowerId(it) }.toSet())
    }
}
