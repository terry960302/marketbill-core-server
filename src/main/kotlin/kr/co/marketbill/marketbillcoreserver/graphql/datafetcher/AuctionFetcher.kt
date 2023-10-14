package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuctionResultDetailOutput
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuctionResultsOutput
import kr.co.marketbill.marketbillcoreserver.service.AuctionService
import kr.co.marketbill.marketbillcoreserver.types.AuctionResultDetailFilterInput
import kr.co.marketbill.marketbillcoreserver.types.AuctionResultFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@DgsComponent
class AuctionFetcher {
    @Autowired
    private lateinit var auctionService: AuctionService

    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.AuctionResult), // v1
    )
    fun getAuctionResults(
        @InputArgument filter: AuctionResultFilterInput?,
        @InputArgument pagination: PaginationInput?,
    ): AuctionResultsOutput {
        val wholesalerId: Long = filter?.wholesalerId?.toLong() ?: 0L
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        val result = this.auctionService.getAuctionResult(
            wholesalerId = wholesalerId,
            auctionDate = LocalDate.now(),
            beforeDays = filter?.beforeDays,
            pageable = pageable
        )

        return AuctionResultsOutput(
            items = result
        )
    }

    @DgsQuery(field = "auctionResultDetail") // v1
    fun getAuctionResultDetail(
        @InputArgument filter: AuctionResultDetailFilterInput,
    ): AuctionResultDetailOutput {
        val result = this.auctionService.getAuctionResultDetail(filter.id.toLong())

        return AuctionResultDetailOutput(
            item = result
        )
    }
}