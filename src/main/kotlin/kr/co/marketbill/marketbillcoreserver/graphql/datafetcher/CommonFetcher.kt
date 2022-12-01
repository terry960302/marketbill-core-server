package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.runBlocking
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.service.MessagingService
import kr.co.marketbill.marketbillcoreserver.types.CommonResponse
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class CommonFetcher {
    @Autowired
    private lateinit var messagingService: MessagingService


    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SendDefaultSms)
    fun sendDefaultSms(@InputArgument to: String, @InputArgument message: String): CommonResponse {
        return runBlocking {
            messagingService.sendDefaultSMS(to, message)
            CommonResponse(success = true)
        }
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.SendVerificationSms)
    fun sendVerificationSms(@InputArgument to: String, @InputArgument code: String): CommonResponse {
        return runBlocking {
            messagingService.sendVerificationSMS(to, code)
            CommonResponse(success = true)
        }
    }
}