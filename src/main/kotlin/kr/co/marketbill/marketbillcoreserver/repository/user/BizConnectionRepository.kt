package kr.co.marketbill.marketbillcoreserver.repository.user

import kr.co.marketbill.marketbillcoreserver.entity.user.BizConnection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BizConnectionRepository : JpaRepository<BizConnection, Long> {
    @Query("SELECT bc.* FROM biz_connections AS bc WHERE bc.retailer_id = :retailerId", nativeQuery = true)
    fun getAllBizConnByRetailerId(retailerId: Long): List<BizConnection>

    @Query("SELECT bc.* FROM biz_connections AS bc WHERE bc.wholesaler_id = :wholesalerId", nativeQuery = true)
    fun getAllBizConnByWholesalerId(wholesalerId: Long): List<BizConnection>
}