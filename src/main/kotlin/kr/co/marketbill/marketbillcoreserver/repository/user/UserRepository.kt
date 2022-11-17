package kr.co.marketbill.marketbillcoreserver.repository.user

import kr.co.marketbill.marketbillcoreserver.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>{
}