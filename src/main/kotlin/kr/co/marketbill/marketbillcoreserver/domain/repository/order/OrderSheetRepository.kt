package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderStatisticOutput
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.types.OrderStatistic
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface OrderSheetRepository : JpaRepository<OrderSheet, Long>, JpaSpecificationExecutor<OrderSheet> {
//    fun findAllByRetailerId(retailerId: Long, pageable: Pageable): Page<OrderSheet>
//    fun findAllByWholesalerId(wholesalerId: Long, pageable: Pageable): Page<OrderSheet>

    @Query(
        "SELECT \n" +
                "\tdate_trunc('day', os.created_at) AS date, \n" +
                "\tCOUNT(DISTINCT(f.flower_type_id)) AS flowerTypeCount,\n" +
                "\tCOUNT(DISTINCT(os.id)) AS orderSheetCount\n" +
                "FROM order_sheets AS os\n" +
                "\tJOIN order_items AS oi ON os.id = oi.order_sheet_id\n" +
                "\tJOIN flowers AS f ON f.id = oi.flower_id\n" +
//                "\tINNER JOIN order_sheet_receipts AS osr ON osr.order_sheet_id = os.id\n" +
                "\tWHERE CAST(os.created_at AS DATE) BETWEEN :fromDate AND :toDate\t\n" +
                "\t\tAND os.wholesaler_id = :wholesalerId\n" +
                "\t\tAND os.$SOFT_DELETE_CLAUSE\n" +
                "GROUP BY date\n " +
                "ORDER BY date DESC", nativeQuery = true
    )
    fun getAllDailyStatistics(
        wholesalerId: Long,
        fromDate: Date,
        toDate: Date,
        pageable: Pageable
    ): Page<OrderStatisticOutput>

}