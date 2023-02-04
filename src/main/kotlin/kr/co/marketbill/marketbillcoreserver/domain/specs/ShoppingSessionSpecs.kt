package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.springframework.data.jpa.domain.Specification

class ShoppingSessionSpecs {
    companion object {
        fun byRetailerId(retailerId: Long?): Specification<ShoppingSession> {
            return Specification<ShoppingSession> { root, query, builder ->
                if(retailerId == null){
                    builder.conjunction()
                }else{
                    val retailer = root.join<ShoppingSession, User>("retailer")
                    builder.equal(retailer.get<Long>("id"), retailerId)
                }
            }
        }
    }
}