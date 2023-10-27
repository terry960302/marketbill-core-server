package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuctionResultDetailOutput
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuctionResultsOutput
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuctionResultForSaleOutput
import kr.co.marketbill.marketbillcoreserver.domain.dto.AuctionResultForSaleDetailOutput
import kr.co.marketbill.marketbillcoreserver.service.AuctionService
import kr.co.marketbill.marketbillcoreserver.types.*
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@DgsComponent
class AuctionResultFetcher {
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
            resultCount = result.count(),
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

    @DgsData.List(
        DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.UpdateAuctionResult),
    )
    fun updateAuctionResult(
        @InputArgument filter: AuctionResultUpdateFilterInput,
    ): AuctionResultDetailOutput {
        val result = this.auctionService.updateAuctionResult(
            id = filter.id.toLong(),
            retailPrice = filter.retailPrice,
            isSoldOut = filter.isSoldOut
        )

        return AuctionResultDetailOutput(
            item = result
        )
    }

    @DgsQuery(field = "auctionResultForSale") // v1
    fun auctionResultForSale(
        filter: AuctionResultForSaleFilterInput,
        pagination: PaginationInput?,
    ): AuctionResultForSaleOutput {
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination, "id")
        val result = this.auctionService.getAuctionResultForSale(
            wholesalerId = filter.wholesalerId.toLong(),
            pageable = pageable
        )

        return AuctionResultForSaleOutput(
            wholesalerId = filter.wholesalerId.toLong(),
            resultCount = result.count(),
            items = result
        )
    }

    @DgsQuery
    fun auctionResultForSaleDetail(
        filter: AuctionResultForSaleDetailFilterInput,
    ): AuctionResultForSaleDetailOutput {
        val result = this.auctionService.getAuctionResultForSaleDetail(filter.id.toLong())

        return AuctionResultForSaleDetailOutput(
            item = result
        )
    }
}