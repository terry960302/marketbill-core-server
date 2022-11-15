package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.dto.SignUpDto
import kr.co.marketbill.marketbillcoreserver.entity.AuthToken
import kr.co.marketbill.marketbillcoreserver.entity.User
import kr.co.marketbill.marketbillcoreserver.entity.UserCredential
import kr.co.marketbill.marketbillcoreserver.repository.AuthTokenRepository
import kr.co.marketbill.marketbillcoreserver.repository.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.repository.UserRepository
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository

    @Autowired
    private lateinit var authTokenRepository: AuthTokenRepository

    @Autowired
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var jwtProvider: JwtProvider


    @Transactional
    fun signUp(input: SignUpDto): AuthTokenDto {
        try {
            val hasUserCred: Boolean = userCredentialRepository.getUserCredentialByPhoneNo(input.phoneNo).isPresent
            if (hasUserCred) throw IllegalArgumentException("There's user who has same phone number. Please check your phone number again.")

            val userInput = User(name = input.name, businessNo = null)
            val user = userRepository.save(userInput)

            val userCredInput = UserCredential(
                phoneNo = input.phoneNo,
                password = passwordEncoder.encode(input.password),
                role = AccountRole.valueOf(input.role),
                user = user
            )

            userCredentialRepository.save(userCredInput)

            val accessToken =
                jwtProvider.generateToken(user.id!!, input.role, JwtProvider.accessExpiration)
            val refreshToken =
                jwtProvider.generateToken(user.id!!, input.role, JwtProvider.refreshExpiration)

            val authTokenInput = AuthToken(
                refreshToken = refreshToken,
                user = user,
            )

            authTokenRepository.save(authTokenInput)

            return AuthTokenDto(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } catch (err: Error) {
            throw Error("Something wrong with this err => ${err.message}")
        }
    }
}