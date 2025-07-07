package kr.co.marketbill.marketbillcoreserver.user.application.command

import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.AccountRole
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId

data class FindConnectionsCommand(
    val userIds: Set<Long>,
    val status: List<ApplyStatus>?,
    val pageInfo: PageInfo
) {
    companion object {
        fun from(
            userIds: Set<Long>,
            status: List<kr.co.marketbill.marketbillcoreserver.types.ApplyStatus>?,
            pageInfo: PageInfo
        ): FindConnectionsCommand {
            return FindConnectionsCommand(
                userIds,
                status?.map { ApplyStatus.from(it) },
                pageInfo
            )
        }
    }
}