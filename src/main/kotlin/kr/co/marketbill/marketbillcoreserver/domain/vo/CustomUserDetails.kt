package kr.co.marketbill.marketbillcoreserver.domain.vo

import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors


class CustomUserDetails(
    private val phoneNo: String,
    private val role: AccountRole,
    private val password: String,
    private val accountNonExpired: Boolean = true,
    private val accountNonLocked: Boolean = true,
    private val credentialsNonExpired: Boolean = true,
    private val enabled: Boolean = true,
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = phoneNo

    override fun isAccountNonExpired(): Boolean = accountNonExpired

    override fun isAccountNonLocked(): Boolean = accountNonLocked

    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired

    override fun isEnabled(): Boolean = enabled
}

