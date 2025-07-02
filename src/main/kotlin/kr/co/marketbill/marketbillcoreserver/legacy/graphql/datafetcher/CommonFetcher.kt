package kr.co.marketbill.marketbillcoreserver.legacy.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.runBlocking
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.application.service.common.MessagingService
import kr.co.marketbill.marketbillcoreserver.types.CommonResponse
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@DgsComponent
class CommonFetcher {
    @Autowired
    private lateinit var messagingService: MessagingService

    @DgsQuery(field = DgsConstants.QUERY.CurrentDateTime)
    fun currentDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }

    @DgsMutation(field = DgsConstants.MUTATION.SendDefaultSms)
    fun sendDefaultSms(@InputArgument to: String, @InputArgument message: String): CommonResponse {
        try {
            return runBlocking {
                messagingService.sendDefaultSMS(to, message)
                CommonResponse(success = true)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    @DgsMutation(field = DgsConstants.MUTATION.SendVerificationSms)
    fun sendVerificationSms(@InputArgument to: String, @InputArgument code: String): CommonResponse {
        try {
            return runBlocking {
                messagingService.sendVerificationSMS(to, code)
                CommonResponse(success = true)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}