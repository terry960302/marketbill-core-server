package kr.co.marketbill.marketbillcoreserver.application.service.common

import com.netflix.graphql.types.errors.ErrorType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.shared.constants.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.constants.MessageTemplate
import kr.co.marketbill.marketbillcoreserver.application.dto.request.MessageReqDto
import kr.co.marketbill.marketbillcoreserver.application.dto.response.MessageResponseDto
import kr.co.marketbill.marketbillcoreserver.shared.exception.CustomException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.time.LocalDateTime


@Service
class MessagingService {
    @Value("\${serverless.messaging.host}")
    private lateinit var baseUrl: String

    @Value("\${spring.config.activate.on-profile}")
    private lateinit var profile: String


    private fun createClient(): WebClient = WebClient
        .builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    private suspend fun requestSms(
        template: MessageTemplate,
        to: String,
        args: List<String>,
    ): MessageResponseDto {
        return try {
            val client = createClient()
            val req = MessageReqDto(
                to = to,
                template = template.toString(),
                args = args,
            )
            req.validate()

            client.post().body(BodyInserters.fromValue(req)).awaitExchange { onMessagingResponse(it) }
        } catch (e: Exception) {
            throw CustomException(
                message = "Error occurred while sending '${template.name}' type of SMS. ${e.message}",
                errorType = ErrorType.INTERNAL,
                errorCode = ErrorCode.SMS_NOT_REACHED,
            )
        }
    }


    suspend fun sendDefaultSMS(to: String, message: String): MessageResponseDto {
        return requestSms(
            template = MessageTemplate.Default,
            to = to,
            args = listOf(message),
        )
    }

    suspend fun sendVerificationSMS(to: String, code: String): MessageResponseDto {
        return requestSms(
            template = MessageTemplate.Verification,
            to = to,
            args = listOf(code),
        )
    }

    suspend fun sendApplyBizConnectionSMS(to: String, retailerName: String): MessageResponseDto {
        val url = generateSmsUrl(AccountRole.WHOLESALER_EMPR)
        return requestSms(
            template = MessageTemplate.ApplyBizConnection,
            to = to,
            args = listOf(retailerName, url),
        )
    }

    suspend fun sendConfirmBizConnectionSMS(to: String, wholesalerName: String): MessageResponseDto {
        val url = generateSmsUrl(AccountRole.RETAILER)
        return requestSms(
            template = MessageTemplate.ConfirmBizConnection,
            to = to,
            args = listOf(wholesalerName, wholesalerName, url),
        )
    }

    suspend fun sendRejectBizConnectionSMS(to: String, wholesalerName: String): MessageResponseDto {
        val url = generateSmsUrl(AccountRole.RETAILER)
        return requestSms(
            template = MessageTemplate.RejectBizConnection,
            to = to,
            args = listOf(wholesalerName, wholesalerName, url),
        )
    }

    suspend fun sendIssueOrderSheetReceiptSMS(
        to: String,
        wholesalerName: String,
        orderNo: String,
    ): MessageResponseDto {
        val url = generateSmsUrl(AccountRole.RETAILER)
        return requestSms(
            template = MessageTemplate.IssueOrderSheetReceipt,
            to = to,
            args = listOf(wholesalerName, orderNo, url),
        )
    }

    private suspend fun onMessagingResponse(res: ClientResponse): MessageResponseDto {

        if (res.statusCode() == HttpStatus.OK) {
            val jsonStr = res.awaitBody<String>()
            val output = Json.decodeFromString<MessageResponseDto>(jsonStr)
            return output
        } else if (res.statusCode() == HttpStatus.ACCEPTED) {
            val output = MessageResponseDto(
                requestId = "-1",
                requestTime = LocalDateTime.now().toString(),
                statusCode = HttpStatus.ACCEPTED.toString(),
                statusName = "Accepted"
            )
            return output
        } else {
            throw Exception(res.awaitBody<String>())
        }
    }

    private fun generateSmsUrl(role: AccountRole): String {
        val subDomain = when (profile) {
            "local", "dev" -> "${profile}."
            else -> ""
        }
        val mainDomain = "marketbill.co.kr"

        val roleGroup = when (role) {
            AccountRole.RETAILER -> "retail"
            else -> "wholesale"
        }
        val source = "marketbill"
        val campaign = "message"

        return "$${subDomain}${mainDomain}/${roleGroup}/signin?" +
                "utm_source=${source}&" +
                "utm_medium=${roleGroup}&" +
                "utm_campaign=${campaign}"
    }
}