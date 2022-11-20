package kr.co.marketbill.marketbillcoreserver.graphql.context

import com.netflix.graphql.dgs.context.DgsCustomContextBuilder
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import org.springframework.stereotype.Component

@Component
class OrderContextBuilder : DgsCustomContextBuilder<OrderContext> {
    override fun build(): OrderContext {
        return OrderContext()
    }
}


class OrderContext {
    var pagination: PaginationInput? = null
}