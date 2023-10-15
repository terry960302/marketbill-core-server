package kr.co.marketbill.marketbillcoreserver.domain.repository.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.AuctionResult
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.InterfaceAuctionResultWithGroupBy
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AuctionResultRepository : JpaRepository<AuctionResult, Long>, JpaSpecificationExecutor<AuctionResult> {
    @Query(
        "SELECT " +
            "ROW_NUMBER() OVER (ORDER BY flower_name, flower_type_name, auction_date) AS id, " +
            "   flower_name as flowerName, " +
            "   flower_type_name as flowerTypeName, " +
            "   auction_date as auctionDate, " +
            "   min(retail_price) as retailPrice, " +
            "   STRING_AGG(id || '|' || flower_grade , ',') AS flowerGrade " +
            "FROM auction_results" +
            "   WHERE deleted_at IS NULL" +
            "   AND wholesaler_id = ?1" +
            "   AND retail_price IS NOT NULL" +
            " GROUP BY flower_name, flower_type_name, auction_date"
        , nativeQuery = true
    )
    fun findGroupByFlowerNameAndAuctionDate(wholesalerId: Long, pageable: Pageable): List<InterfaceAuctionResultWithGroupBy> = emptyList()
}