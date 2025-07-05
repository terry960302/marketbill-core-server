package kr.co.marketbill.marketbillcoreserver.shared.application.port.out

import org.springframework.security.core.userdetails.UserDetails

interface UserDetailsPort {
    fun loadUserById(userId: Long): UserDetails
}