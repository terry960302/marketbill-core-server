package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.UserCredential
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType

class UserSpecs {
    companion object {

        fun hasRoles(roles: List<AccountRole>?): Specification<User> {
            return Specification<User> { root, query, builder ->
                if (roles == null) {
                    builder.conjunction()
                } else {
                    val userCred: Join<User, UserCredential> = root.join("userCredential")
                    userCred.get<AccountRole>("role").`in`(roles)
                }
            }
        }

//        fun leftJoinBizConn(userId: Long?, role: AccountRole?): Specification<User> {
//            return Specification<User> { root, query, builder ->
//                if (userId == null || role == null) {
//                    builder.conjunction()
//                } else {
//                    if (role == AccountRole.RETAILER) {
//                        val bizConn = root.join<User, BizConnection>("retailerToWholesaler", JoinType.LEFT)
//                        builder.equal(bizConn.get<Long>("retailer_id"), userId)
//                    } else {
//                        val bizConn = root.join<User, BizConnection>("wholesalerToRetailer", JoinType.LEFT)
//                        builder.equal(bizConn.get<Long>("wholesaler_id"), userId)
//                    }
//
//                }
//            }
//        }

        fun exclude(id: Long?): Specification<User> {
            return Specification<User> { root, query, builder ->
                if (id == null) {
                    builder.conjunction()
                } else {
                    builder.notEqual(root.get<Long>("id"), id)
                }
            }
        }
    }
}