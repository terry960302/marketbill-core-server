package kr.co.marketbill.marketbillcoreserver.graphql.context

import com.netflix.graphql.dgs.context.DgsCustomContextBuilder
import kr.co.marketbill.marketbillcoreserver.domain.dto.BizConnectionContextInput
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderItemContextInput
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderSheetReceiptContextInput
import org.springframework.stereotype.Component

@Component
class CustomContextBuilder : DgsCustomContextBuilder<CustomContext> {
    override fun build(): CustomContext {
        return CustomContext()
    }
}

// 한 쿼리에서 context 를 독립적으로 사용하고 싶으면 아래와 같이 필드별로 분기처리 필요(이렇게 안하면, context 에 저장된 값이 밀려들어가는 현상이 발생)
class CustomContext {
    val appliedConnectionsInput: BizConnectionContextInput = BizConnectionContextInput()
    val receivedConnectionsInput: BizConnectionContextInput = BizConnectionContextInput()
    val orderItemsInput: OrderItemContextInput = OrderItemContextInput()
    val orderSheetReceiptsInput : OrderSheetReceiptContextInput = OrderSheetReceiptContextInput()
}


