package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface OrderSheetRepositoryCustom {
    fun findAllWithFilters(pageable: Pageable, userId: Long?, role: AccountRole?, date: LocalDate?): Page<OrderSheet>
}