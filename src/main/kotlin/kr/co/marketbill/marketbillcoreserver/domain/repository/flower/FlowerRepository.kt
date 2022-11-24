package kr.co.marketbill.marketbillcoreserver.domain.repository.flower

import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FlowerRepository : JpaRepository<Flower, Long>, JpaSpecificationExecutor<Flower> {
    @Query(
        "SELECT * FROM flowers AS f " +
                "JOIN flower_types AS ft ON ft.id = f.flower_type_id " +
                "JOIN bidding_flowers AS bf ON bf.flower_id = f.id " +
                "WHERE CAST(bf.bidding_date AS date) = :currentDate " +
                "AND f.$SOFT_DELETE_CLAUSE", nativeQuery = true
    )
    fun getAllBuyableFlowers(currentDate: Date, pageable : Pageable): Page<Flower>

    @Query("SELECT COUNT(*) FROM flowers AS f WHERE f.name LIKE %:keyword% AND f.$SOFT_DELETE_CLAUSE", nativeQuery = true)
    fun getSearchFlowersCount(keyword : String) : Long
}