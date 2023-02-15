package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.domain.dto.FlowersOutput
import kr.co.marketbill.marketbillcoreserver.service.FlowerService
import kr.co.marketbill.marketbillcoreserver.types.FlowerFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@DgsComponent
class FlowerFetcher {
    @Autowired
    private lateinit var flowerService: FlowerService

    @DgsQuery(field = DgsConstants.QUERY.GetFlowers)
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