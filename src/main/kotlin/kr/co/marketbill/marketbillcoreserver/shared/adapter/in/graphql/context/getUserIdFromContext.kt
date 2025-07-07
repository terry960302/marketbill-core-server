package kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.context

import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

fun getUserIdFromContext(): UserId {
    // TODO: 실제 JWT 토큰에서 사용자 ID 추출
    // 현재는 임시로 하드코딩된 값 사용
    return UserId(1L)
} 