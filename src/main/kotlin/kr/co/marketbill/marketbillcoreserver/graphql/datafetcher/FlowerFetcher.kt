package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.service.FlowerService
import kr.co.marketbill.marketbillcoreserver.types.FlowerFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDate

@DgsComponent
class FlowerFetcher {
    @Autowired
    private lateinit var flowerService: FlowerService

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetFlowers)
    fun getFlowers(
        @InputArgument filter: FlowerFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<Flower> {
        var fromDate: LocalDate? = null
        var toDate: LocalDate? = null
        var keyword: String? = null
        var pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)

        if (filter != null) {
            if (filter.dateRange != null) {
                fromDate = LocalDate.parse(filter.dateRange.fromDate)
                toDate = LocalDate.parse(filter.dateRange.toDate)
            }
            if (filter.keyword != null) {
                keyword = filter.keyword
            }
        }

        if (pagination != null) {
            pageable = PageRequest.of(pagination.page!!, pagination.size!!)
        }

        val res = flowerService.getFlowers(fromDate, toDate, keyword, pageable)
        return res.map {
            it.totalResultCount = res.totalElements
            it
        }
    }
}