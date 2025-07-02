package kr.co.marketbill.marketbillcoreserver.flower.application.result

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult

data class FlowerSearchResult(
    val flowers: List<FlowerResult>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val hasNext: Boolean
) {
    companion object {
        fun from(pageResult: PageResult<Flower>): FlowerSearchResult {
            return FlowerSearchResult(
                flowers = pageResult.content.map { FlowerResult.from(it) },
                totalElements = pageResult.totalElements,
                totalPages = pageResult.totalPages,
                currentPage = pageResult.pageInfo.page,
                hasNext = pageResult.hasNext
            )
        }
    }
}
