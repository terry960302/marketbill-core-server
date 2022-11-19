package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.data.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.service.FlowerService
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@DgsComponent
class FlowerDataFetcher {
    @Autowired
    private lateinit var flowerService: FlowerService

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetAllBuyableFlowers)
    fun getAllBuyableFlowers(@InputArgument pagination : PaginationInput?): Page<Flower> {
        var pageable: Pageable =  PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        if(pagination != null){
            pageable = PageRequest.of(pagination.page!!, pagination.size!!)
        }
        return flowerService.getAllBuyableFlowers(pageable)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.SearchFlowers)
    fun searchFlowers(@InputArgument keyword: String, @InputArgument pagination: PaginationInput?): Page<Flower> {
        var pageable: Pageable =  PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        if(pagination != null){
            pageable = PageRequest.of(pagination.page!!, pagination.size!!)
        }
        return flowerService.searchFlowers(keyword, pageable)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetSearchFlowersCount)
    fun getSearchFlowersCount(@InputArgument keyword: String): Int {
        return flowerService.getSearchFlowersTotalCount(keyword)
    }
}