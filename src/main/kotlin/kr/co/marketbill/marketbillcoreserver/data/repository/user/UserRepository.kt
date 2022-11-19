package kr.co.marketbill.marketbillcoreserver.data.repository.user

import kr.co.marketbill.marketbillcoreserver.data.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>{
}