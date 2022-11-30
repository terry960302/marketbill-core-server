package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import kr.co.marketbill.marketbillcoreserver.constants.SOFT_DELETE_CLAUSE
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
interface OrderSheetRepository : JpaRepository<OrderSheet, Long>, JpaSpecificationExecutor<OrderSheet> {

    @Query(
        "SELECT \n" +
                "\tdate_trunc('day', os.created_at) AS date, \n" +
                "\tCOUNT(DISTINCT(f.flower_type_id)) AS flowerTypesCount,\n" +
                "\tCOUNT(DISTINCT(os.id)) AS orderSheetsCount\n" +
                "FROM order_sheets AS os\n" +
                "\tJOIN order_items AS oi ON os.id = oi.order_sheet_id\n" +
                "\tJOIN flowers AS f ON f.id = oi.flower_id\n" +
                // TODO: 영수증 발행 병합 후 주석 풀어야함(영수증 있는 경우만 나오게 필터건 쿼리)
//                "\tINNER JOIN order_sheet_receipts AS osr ON osr.order_sheet_id = os.id\n" +
                "\tWHERE CAST(os.created_at AS DATE) BETWEEN :fromDate AND :toDate\t\n" +
                "\t\tAND os.wholesaler_id = :wholesalerId\n" +
                "\t\tAND os.$SOFT_DELETE_CLAUSE\n" +
                "GROUP BY date\n " +
                "ORDER BY date DESC", nativeQuery = true
    )
    fun getAllDailyOrderSheetsAggregates(
        wholesalerId: Long,
        fromDate: Date,
        toDate: Date,
        pageable: Pageable
    ): Page<OrderSheetsAggregate>

    @Query(
        "SELECT \n" +
                "\tdate_trunc('day', MIN(os.created_at)) AS date, " +
                "\tCOUNT(DISTINCT(os.id)) AS orderSheetsCount, \n" +
                "\tCOUNT(DISTINCT(ft.id)) AS flowerTypesCount \n" +
                "FROM order_sheets AS os\n" +
                "\tJOIN order_items AS oi ON oi.order_sheet_id = os.id\n" +
                "\tJOIN flowers AS f ON f.id = oi.flower_id\n" +
                "\tJOIN flower_types AS ft ON ft.id = f.flower_type_id\n" +
                "WHERE os.wholesaler_id = :wholesalerId\n" +
                "\tAND os.$SOFT_DELETE_CLAUSE\n " +
                "\tAND CAST(os.created_at AS DATE) = :date\n", nativeQuery = true
    )
    fun getDailyOrderSheetsAggregate(wholesalerId: Long, date: Date): OrderSheetsAggregate

}