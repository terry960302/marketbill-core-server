package kr.co.marketbill.marketbillcoreserver.shared.util

import kr.co.marketbill.marketbillcoreserver.shared.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.shared.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import kr.co.marketbill.marketbillcoreserver.types.Sort as GraphqlSort

class GqlDtoConverter {
    companion object {
        fun convertPaginationInputToPageable(
            pagination: PaginationInput?,
            sortBy: String? = "createdAt"
        ): Pageable {
            return if (pagination != null) {
                val sort = if (pagination.sort == GraphqlSort.ASCEND) {
                    Sort.by(sortBy).ascending()
                } else {
                    Sort.by(sortBy).descending()
                }
                PageRequest.of(pagination.page!!, pagination.size!!, sort)
            } else {
                PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by(sortBy).descending())
            }
        }
    }
}