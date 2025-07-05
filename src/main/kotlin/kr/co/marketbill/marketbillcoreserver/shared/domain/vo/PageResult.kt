package kr.co.marketbill.marketbillcoreserver.shared.domain.vo

data class PageResult<T>(val content: List<T>, val pageInfo: PageInfo, val totalElements: Long) {
    val totalPages: Int =
            if (pageInfo.size == 0) 1
            else ((totalElements + pageInfo.size - 1) / pageInfo.size).toInt()
    val hasNext: Boolean = pageInfo.page + 1 < totalPages
    val hasPrevious: Boolean = pageInfo.page > 0

    fun <R> map(transform: (T) -> R): PageResult<R> {
        return PageResult(
                content = content.map(transform),
                pageInfo = pageInfo,
                totalElements = totalElements
        )
    }

    companion object {
        fun <T> empty(pageInfo: PageInfo): PageResult<T> {
            return PageResult(emptyList(), pageInfo, 0L)
        }

        fun <T> from(
                content: List<T>,
                totalElements: Long,
                totalPages: Int,
                currentPage: Int,
                hasNext: Boolean
        ): PageResult<T> {
            val pageInfo = PageInfo.from(currentPage, content.size)
            return PageResult(content, pageInfo, totalElements)
        }
    }
}
