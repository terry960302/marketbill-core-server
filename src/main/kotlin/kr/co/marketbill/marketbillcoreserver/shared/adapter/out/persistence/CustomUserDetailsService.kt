package kr.co.marketbill.marketbillcoreserver.shared.adapter.out.persistence

import kr.co.marketbill.marketbillcoreserver.shared.application.port.out.UserDetailsPort
import kr.co.marketbill.marketbillcoreserver.shared.domain.model.CustomUserDetails
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import kr.co.marketbill.marketbillcoreserver.user.application.port.outbound.UserRepository
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService, UserDetailsPort {
    override fun loadUserByUsername(userId: String?): UserDetails {
        val user =
            userRepository.findById(UserId.from(userId!!.toLong())) ?: throw MarketbillException(ErrorCode.NO_USER)
        return CustomUserDetails(userId = user.id!!, phoneNo = user.phoneNumber, role = user.role)
    }

    override fun loadUserById(userId: Long): UserDetails {
        val user =
            userRepository.findById(UserId.from(userId)) ?: throw MarketbillException(ErrorCode.NO_USER)
        return CustomUserDetails(userId = user.id!!, phoneNo = user.phoneNumber, role = user.role)    }
}