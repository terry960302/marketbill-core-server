package kr.co.marketbill.marketbillcoreserver.shared.domain.vo

import kr.co.marketbill.marketbillcoreserver.types.PaginationInput

data class PageInfo(val page: Int, val size: Int) {
    init {
        require(page >= 0) { "페이지는 0 이상이어야 합니다" }
        require(size > 0) { "페이지 크기는 양수여야 합니다" }
    }

    companion object {
        fun from(page: Int, size: Int): PageInfo {
            return PageInfo(page, size)
        }
    }
}
