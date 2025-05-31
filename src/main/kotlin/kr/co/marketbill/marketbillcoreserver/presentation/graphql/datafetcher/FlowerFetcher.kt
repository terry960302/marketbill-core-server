package kr.co.marketbill.marketbillcoreserver.presentation.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.application.dto.response.FlowersOutput
import kr.co.marketbill.marketbillcoreserver.application.service.flower.FlowerService
import kr.co.marketbill.marketbillcoreserver.shared.util.GqlDtoConverter
import kr.co.marketbill.marketbillcoreserver.types.FlowerFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@DgsComponent
class FlowerFetcher {
    @Autowired
    private lateinit var flowerService: FlowerService

    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetFlowers), // v1
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.Flowers) // v2
    )
    fun getFlowers(
        @InputArgument filter: FlowerFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): FlowersOutput {
        var fromDate: LocalDate? = null
        var toDate: LocalDate? = null
        var keyword: String? = null
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        if (filter != null) {
            if (filter.dateRange != null) {
                fromDate = LocalDate.parse(filter.dateRange.fromDate)
                toDate = LocalDate.parse(filter.dateRange.toDate)
            }
            if (filter.keyword != null) {
                keyword = filter.keyword
            }
        }

        val res = flowerService.getFlowers(fromDate, toDate, keyword, pageable)
        return FlowersOutput(
            resultCount = res.totalElements,
            items = res,
        )
    }
}