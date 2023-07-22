package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem>, CartItemRepositoryCustom {
    companion object {
        private const val RANK_FIELD = "row_num"
        private const val WHERE_CLAUSE = "WHERE deleted_at IS NULL " +
                "AND ordered_at IS NULL " +
                "AND session_id IS NOT NULL " +
                "AND session_id IN :sessionIds"
        private const val RANKED_SUB_QUERY =
            "SELECT *,\n" +
                    "ROW_NUMBER() OVER (PARTITION BY session_id) as $RANK_FIELD\n" +
                    "FROM cart_items\n" +
                    WHERE_CLAUSE
        private const val PAGINATED_QUERY =
            "SELECT * FROM\n" +
                    "($RANKED_SUB_QUERY) as data\n" +
                    "WHERE $RANK_FIELD BETWEEN :startRow AND :endRow"

//        private const val GROUPED_COUNT_QUERY =
//            "SELECT session_id, COUNT(id)\n" +
//                    "FROM cart_items\n" +
//                    "$WHERE_CLAUSE\n" +
//                    "GROUP BY session_id"
    }

    fun findAllByRetailerId(retailerId: Long, pageable: Pageable): Page<CartItem>

    @Query(PAGINATED_QUERY, nativeQuery = true)
    fun getAllPaginatedCartItemsBySessionIds(sessionIds: List<Long>, startRow: Int, endRow: Int): List<CartItem>

//    @Query(GROUPED_COUNT_QUERY, nativeQuery = true)
//    fun countTotalPaginatedCartItemsBySessionIds(sessionIds: List<Long>): List<GroupedCartItemCountDto>
}