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

    companion object {
        private const val FIELD_DATE = "date"
        private const val FIELD_FLOWER_TYPES_COUNT = "flowerTypesCount"
        private const val FIELD_ORDERSHEETS_COUNT = "orderSheetsCount"

        const val OPTIMIZED_FIND_ONE_QUERY =
            "SELECT \n" +
                    "\tdate_trunc('day', MIN(os.created_at)) AS $FIELD_DATE, " +
                    "\tCOUNT(DISTINCT(os.id)) AS $FIELD_ORDERSHEETS_COUNT, \n" +
                    "\tCOUNT(DISTINCT(f.flower_type_id)) AS $FIELD_FLOWER_TYPES_COUNT \n" +
                    "FROM \n" +
                    "\t(SELECT * FROM order_sheets WHERE $SOFT_DELETE_CLAUSE AND wholesaler_id = :wholesalerId) AS os\n" +
                    "\tJOIN (SELECT * FROM order_items WHERE $SOFT_DELETE_CLAUSE) AS oi \n" +
                    "\t\tON os.id = oi.order_sheet_id\n" +
                    "\tJOIN (SELECT * FROM flowers WHERE $SOFT_DELETE_CLAUSE) AS f \n" +
                    "\t\tON f.id = oi.flower_id\n" +
                    "WHERE CAST(os.created_at AS DATE) = :date\n"

        const val OPTIMIZED_FIND_ALL_QUERY =
            "SELECT\n" +
                    "\tdate_trunc('day', os.created_at) AS $FIELD_DATE,\n" +
                    "\tCOUNT(DISTINCT(f.flower_type_id)) AS $FIELD_FLOWER_TYPES_COUNT,\n" +
                    "\tCOUNT(DISTINCT(os.id)) AS $FIELD_ORDERSHEETS_COUNT\n" +
                    "FROM \n" +
                    "\t(SELECT * FROM order_sheets WHERE $SOFT_DELETE_CLAUSE AND wholesaler_id = :wholesalerId) AS os\n" +
                    "\tJOIN \n" +
                    "\t\t(SELECT * FROM order_items WHERE $SOFT_DELETE_CLAUSE) AS oi \n" +
                    "\t\tON os.id = oi.order_sheet_id\n" +
                    "\tJOIN (SELECT * FROM flowers WHERE $SOFT_DELETE_CLAUSE) AS f \n" +
                    "\t\tON f.id = oi.flower_id\n" +
                    // TODO: 영수증 발행 병합 후 주석 풀어야함(영수증 있는 경우만 나오게 필터건 쿼리)
                    "\tINNER JOIN (SELECT * FROM order_sheet_receipts WHERE $SOFT_DELETE_CLAUSE) AS osr \n" +
                    "\t\tON osr.order_sheet_id = os.id\n" +
                    "\tWHERE CAST(os.created_at AS DATE) BETWEEN :fromDate AND :toDate\n" +
                    "\tGROUP BY date"
    }

    @Query(OPTIMIZED_FIND_ALL_QUERY, nativeQuery = true)
    fun getAllDailyOrderSheetsAggregates(
        wholesalerId: Long,
        fromDate: Date,
        toDate: Date,
        pageable: Pageable
    ): Page<OrderSheetsAggregate>

    @Query(OPTIMIZED_FIND_ONE_QUERY, nativeQuery = true)
    fun getDailyOrderSheetsAggregate(wholesalerId: Long, date: Date): OrderSheetsAggregate
}