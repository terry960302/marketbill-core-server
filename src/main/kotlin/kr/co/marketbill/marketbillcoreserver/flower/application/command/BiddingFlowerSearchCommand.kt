package kr.co.marketbill.marketbillcoreserver.flower.application.command

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerSearchCriteria
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.types.FlowerFilterInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class BiddingFlowerSearchCommand(
    val ids: Set<Long>
) {

}
