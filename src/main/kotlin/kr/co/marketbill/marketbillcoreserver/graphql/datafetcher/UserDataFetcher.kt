package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.dto.AuthTokenDto
import kr.co.marketbill.marketbillcoreserver.dto.SignInDto
import kr.co.marketbill.marketbillcoreserver.dto.SignUpDto
import kr.co.marketbill.marketbillcoreserver.entity.User
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.service.UserService
import kr.co.marketbill.marketbillcoreserver.types.SignInInput
import kr.co.marketbill.marketbillcoreserver.types.SignUpInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestHeader
import java.util.*

@DgsComponent
class UserDataFetcher {

    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.Me)
    fun me(@RequestHeader("Authorization") authorization : String) : Optional<User> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        return userService.me(userId)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SignUp)
    fun signUp(@InputArgument input : SignUpInput) : AuthTokenDto{
        return userService.signUp(input)
    }
    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SignIn)
    fun signIn(@InputArgument input : SignInInput) : AuthTokenDto{
        return userService.signIn(input)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.DeleteUser)
    fun deleteUser(@InputArgument userId : Long) : Boolean{
        return userService.deleteUser(userId)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.DeleteAuthToken)
    fun deleteAuthToken(@InputArgument tokenId : Long) : Boolean{
        return userService.deleteAuthToken(tokenId)
    }
}