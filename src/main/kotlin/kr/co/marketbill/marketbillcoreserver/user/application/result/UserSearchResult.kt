package kr.co.marketbill.marketbillcoreserver.user.application.result

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult

data class UserSearchResult(val users: List<UserResult>, val pageResult: PageResult<UserResult>) {
    companion object {
        fun from(
                users: List<UserResult>,
                totalElements: Long,
                totalPages: Int,
                currentPage: Int,
                hasNext: Boolean
        ): UserSearchResult {
            return UserSearchResult(
                    users = users,
                    pageResult =
                            PageResult.from(
                                    content = users,
                                    totalElements = totalElements,
                                    totalPages = totalPages,
                                    currentPage = currentPage,
                                    hasNext = hasNext
                            )
            )
        }
    }
}
