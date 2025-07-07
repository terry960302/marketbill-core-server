package kr.co.marketbill.marketbillcoreserver.flower.adapter.`in`.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.flower.adapter.`in`.graphql.mapper.FlowerOutputMapper
import kr.co.marketbill.marketbillcoreserver.flower.application.command.FlowerSearchCommand
import kr.co.marketbill.marketbillcoreserver.flower.application.service.FlowerService
import kr.co.marketbill.marketbillcoreserver.types.FlowerFilterInput
import kr.co.marketbill.marketbillcoreserver.types.FlowersOutput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import java.time.LocalDate

@DgsComponent
class FlowerFetcher(private val flowerService: FlowerService, private val mapper: FlowerOutputMapper) {
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.Flowers)
    )
    fun flowers(
        @InputArgument filter: FlowerFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): FlowersOutput {
        val command = FlowerSearchCommand.fromGraphql(filter, pagination)
        val result = flowerService.getFlowers(command)
        return mapper.toOutput(result)
    }
}