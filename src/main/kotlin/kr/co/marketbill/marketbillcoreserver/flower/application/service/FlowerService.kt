package kr.co.marketbill.marketbillcoreserver.flower.application.service

import kr.co.marketbill.marketbillcoreserver.flower.application.command.BiddingFlowerSearchCommand
import kr.co.marketbill.marketbillcoreserver.flower.application.command.FlowerSearchCommand
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerSearchResult
import kr.co.marketbill.marketbillcoreserver.flower.application.usecase.BiddingFlowerSearchUseCase
import kr.co.marketbill.marketbillcoreserver.flower.application.usecase.FlowerSearchUseCase
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FlowerService(
    private val flowerSearchUseCase: FlowerSearchUseCase,
    private val biddingFlowerSearchUseCase: BiddingFlowerSearchUseCase,
) {
    fun getFlowers(command: FlowerSearchCommand): FlowerSearchResult {
        return flowerSearchUseCase.execute(command);
    }

    fun findBiddingFlowersByFlowerIds(command: BiddingFlowerSearchCommand): MutableMap<FlowerId, List<BiddingFlower>> {
        val biddingFlowers = biddingFlowerSearchUseCase.execute(command)
        return biddingFlowers.groupBy { it.flower!!.id!! }.toMutableMap()
    }

}