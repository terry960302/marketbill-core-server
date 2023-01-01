package kr.co.marketbill.marketbillcoreserver.service

import com.netflix.graphql.types.errors.ErrorType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.co.marketbill.marketbillcoreserver.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.constants.MessageTemplate
import kr.co.marketbill.marketbillcoreserver.domain.dto.MessageReqDto
import kr.co.marketbill.marketbillcoreserver.domain.dto.MessageResponseDto
import kr.co.marketbill.marketbillcoreserver.graphql.error.CustomException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private val logger: Logger = LoggerFactory.getLogger(MessagingService::class.java)

    private fun createClient(): WebClient {
        return WebClient
            .builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }


    suspend fun sendDefaultSMS(to: String, message: String): MessageResponseDto {
        try {
            val client = createClient()
            val req = MessageReqDto(
                to = to,
                template = MessageTemplate.Default.toString(),
                args = listOf(message)
            )

            val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange { onMessagingResponse(it) }
            logger.info(res.toString())
            return res
        } catch (e: Exception) {
            throw CustomException(
                message = "Error occurred while sending 'Default' type of SMS. ${e.message}",
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.SMS_NOT_REACHED
            )
        }
    }

    suspend fun sendVerificationSMS(to: String, code: String): MessageResponseDto {
        try {
            val client = createClient()
            val req = MessageReqDto(
                to = to,
                template = MessageTemplate.Verification.toString(),
                args = listOf(code)
            )
            val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange { onMessagingResponse(it) }
            logger.info(res.toString())
            return res
        } catch (e: Exception) {
            throw CustomException(
                message = "Error occurred while sending 'Verification' type of SMS. ${e.message}",
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.SMS_NOT_REACHED
            )
        }
    }

    suspend fun sendApplyBizConnectionSMS(to: String, retailerName: String, url: String): MessageResponseDto {
        try {
            val client = createClient()
            val req = MessageReqDto(
                to = to,
                template = MessageTemplate.ApplyBizConnection.toString(),
                args = listOf(retailerName, url)
            )
            val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange { onMessagingResponse(it) }
            logger.info(res.toString())
            return res
        } catch (e: Exception) {
            throw CustomException(
                message = "Error occurred while sending 'ApplyBizConnection' type of SMS. ${e.message}",
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.SMS_NOT_REACHED
            )
        }
    }

    suspend fun sendConfirmBizConnectionSMS(to: String, wholesalerName: String, url: String): MessageResponseDto {
        try {
            val client = createClient()
            val req = MessageReqDto(
                to = to,
                template = MessageTemplate.ConfirmBizConnection.toString(),
                args = listOf(wholesalerName, wholesalerName, url)
            )
            val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange { onMessagingResponse(it) }
            logger.info(res.toString())
            return res
        } catch (e: Exception) {
            throw CustomException(
                message = "Error occurred while sending 'ConfirmBizConnection' type of SMS. ${e.message}",
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.SMS_NOT_REACHED
            )
        }
    }

    suspend fun sendRejectBizConnectionSMS(to: String, wholesalerName: String): MessageResponseDto {
        try {
            val client = createClient()
            val req = MessageReqDto(
                to = to,
                template = MessageTemplate.RejectBizConnection.toString(),
                args = listOf(wholesalerName, wholesalerName)
            )
            val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange { onMessagingResponse(it) }
            logger.info(res.toString())
            return res
        } catch (e: Exception) {
            throw CustomException(
                message = "Error occurred while sending 'RejectBizConnection' type of SMS. ${e.message}",
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.SMS_NOT_REACHED
            )
        }
    }

    suspend fun sendIssueOrderSheetReceiptSMS(
        to: String,
        wholesalerName: String,
        orderNo: String,
        url: String
    ): MessageResponseDto {
        try {
            val client = createClient()
            val req = MessageReqDto(
                to = to,
                template = MessageTemplate.IssueOrderSheetReceipt.toString(),
                args = listOf(wholesalerName, orderNo, url)
            )
            val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange { onMessagingResponse(it) }
            logger.info(res.toString())
            return res
        } catch (e: Exception) {
            throw CustomException(
                message = "Error occurred while sending 'IssueOrderSheetReceipt' type of SMS. ${e.message}",
                errorType = ErrorType.INTERNAL,
                errorCode = CustomErrorCode.SMS_NOT_REACHED
            )
        }
    }

    suspend fun onMessagingResponse(res: ClientResponse): MessageResponseDto {
        return if (res.statusCode() == HttpStatus.OK) {
            val jsonStr = res.awaitBody<String>()
            Json.decodeFromString<MessageResponseDto>(jsonStr)
        } else if (res.statusCode() == HttpStatus.ACCEPTED) {
            MessageResponseDto(
                requestId = "-1",
                requestTime = LocalDateTime.now().toString(),
                statusCode = HttpStatus.ACCEPTED.toString(),
                statusName = "Accepted"
            )
        } else {
            throw Exception(res.awaitBody<String>())
        }
    }
}