package kr.co.marketbill.marketbillcoreserver.util

import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class GqlDtoConverter {
    companion object {
        fun convertPaginationInputToPageable(
            pagination: PaginationInput?,
            fieldToSort: String? = "createdAt"
        ): Pageable {
            var pageable: Pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
            if (pagination != null) {
                val sort = if (pagination.sort == kr.co.marketbill.marketbillcoreserver.types.Sort.ASCEND) {
                    Sort.by(fieldToSort).ascending()
                } else {
                    Sort.by(fieldToSort).descending()
                }
                pageable = PageRequest.of(pagination.page!!, pagination.size!!, sort)
            }
            return pageable
        }
    }
}