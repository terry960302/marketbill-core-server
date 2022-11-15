package kr.co.marketbill.marketbillcoreserver.repository

import kr.co.marketbill.marketbillcoreserver.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>{
}