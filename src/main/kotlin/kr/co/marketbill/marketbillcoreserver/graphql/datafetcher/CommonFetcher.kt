package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.service.MessagingService
import kr.co.marketbill.marketbillcoreserver.types.CommonResponse
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class CommonFetcher {
    @Autowired
    private lateinit var messagingService: MessagingService

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