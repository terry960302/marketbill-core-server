package kr.co.marketbill.marketbillcoreserver.flower.application.service

import kr.co.marketbill.marketbillcoreserver.flower.adapter.`in`.graphql.mapper.FlowerOutputMapper
import kr.co.marketbill.marketbillcoreserver.flower.application.command.FlowerSearchCommand
import kr.co.marketbill.marketbillcoreserver.flower.application.result.FlowerSearchResult
import kr.co.marketbill.marketbillcoreserver.flower.application.usecase.FlowerSearchUseCase
import kr.co.marketbill.marketbillcoreserver.types.FlowersOutput
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FlowerService(
    private val flowerSearchUseCase: FlowerSearchUseCase
) {
    @Transactional(readOnly = true)
    fun getFlowers(command: FlowerSearchCommand): FlowersOutput {
        val flowers: FlowerSearchResult = flowerSearchUseCase.execute(command);
        return FlowerOutputMapper.mapToFlowersOutput(flowers)
    }

}