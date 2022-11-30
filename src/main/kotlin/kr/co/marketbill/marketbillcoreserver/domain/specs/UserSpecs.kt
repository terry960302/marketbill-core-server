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

        fun isName(name: String?): Specification<User> {
            return Specification<User> { root, query, builder ->
                if (name == null) {
                    builder.conjunction()
                } else {
                    builder.equal(root.get<String>("name"), name)
                }
            }
        }


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