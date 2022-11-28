package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.BatchCartToOrderLogs
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BatchCartToOrderLogsRepository : JpaRepository<BatchCartToOrderLogs, Long>{
}