package kr.co.marketbill.marketbillcoreserver.domain.vo

import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors


class CustomUserDetails(
    private val phoneNo: String, private val role: AccountRole
) : UserDetails {


    override fun getAuthorities(): Collection<GrantedAuthority> {
        return arrayListOf(role).stream().map { role -> SimpleGrantedAuthority("ROLE_$role") }
            .collect(Collectors.toSet())
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = phoneNo

    override fun isAccountNonExpired(): Boolean = isAccountNonExpired

    override fun isAccountNonLocked(): Boolean = isAccountNonLocked

    override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired

    override fun isEnabled(): Boolean = isEnabled
}

