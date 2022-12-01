package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.UserCredential
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class UserCredSpecs {
    companion object {
        fun byPhoneNo(phoneNo: String?): Specification<UserCredential> {
            return Specification<UserCredential> { root, query, builder ->
                if (phoneNo == null) {
                    builder.conjunction()
                } else {
                    builder.equal(root.get<String>("phone_no"), phoneNo)
                }
            }
        }

        fun byPassword(password: String?): Specification<UserCredential> {
            return Specification<UserCredential> { root, query, builder ->
                if (password == null) {
                    builder.conjunction()
                } else {
                    builder.equal(root.get<String>("password"), password)
                }
            }
        }


    }
}