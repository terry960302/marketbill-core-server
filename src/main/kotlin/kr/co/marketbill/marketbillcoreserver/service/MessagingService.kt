package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.MessageType
import kr.co.marketbill.marketbillcoreserver.domain.dto.MessageReqDto
import kr.co.marketbill.marketbillcoreserver.graphql.error.InternalErrorException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange


@Service
class MessagingService {
    @Value("\${serverless.messaging.host}")
    private lateinit var baseUrl: String

    private fun createClient(): WebClient {
        return WebClient
            .builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    suspend fun sendDefaultSMS(to: String, message: String): MessageReqDto {
        val client = createClient()
        val req = MessageReqDto(
            to = to,
            messageType = MessageType.Default.toString(),
            args = listOf(message)
        )
        val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange {
            it.awaitBody<String>()
        }

        if (res.contains("requestId") || res.contains("requestTime")) {
            return req
        } else {
            throw InternalErrorException("Error occurred while sending 'Default' type of SMS.")
        }
    }

    suspend fun sendVerificationSMS(to: String, code: String): MessageReqDto {
        val client = createClient()
        val req = MessageReqDto(
            to = to,
            messageType = MessageType.Verification.toString(),
            args = listOf(code)
        )
        val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange {
            it.awaitBody<String>()
        }

        if (res.contains("requestId") || res.contains("requestTime")) {
            return req
        } else {
            throw InternalErrorException("Error occurred while sending 'Verification' type of SMS.")
        }
    }

    suspend fun sendApplyBizConnectionSMS(to: String, retailerName: String, url: String): MessageReqDto {
        val client = createClient()
        val req = MessageReqDto(
            to = to,
            messageType = MessageType.ApplyBizConnection.toString(),
            args = listOf(retailerName, url)
        )
        val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange {
            it.awaitBody<String>()
        }

        if (res.contains("requestId") || res.contains("requestTime")) {
            return req
        } else {
            throw InternalErrorException("Error occurred while sending 'ApplyBizConnection' type of SMS.")
        }
    }

    suspend fun sendConfirmBizConnectionSMS(to: String, wholesalerName: String, url: String): MessageReqDto {
        val client = createClient()
        val req = MessageReqDto(
            to = to,
            messageType = MessageType.ConfirmBizConnection.toString(),
            args = listOf(wholesalerName, wholesalerName, url)
        )
        val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange {
            it.awaitBody<String>()
        }

        if (res.contains("requestId") || res.contains("requestTime")) {
            return req
        } else {
            throw InternalErrorException("Error occurred while sending 'ConfirmBizConnection' type of SMS.")
        }
    }

    suspend fun sendRejectBizConnectionSMS(to: String, wholesalerName: String): MessageReqDto {
        val client = createClient()
        val req = MessageReqDto(
            to = to,
            messageType = MessageType.RejectBizConnection.toString(),
            args = listOf(wholesalerName, wholesalerName)
        )
        val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange {
            it.awaitBody<String>()
        }

        if (res.contains("requestId") || res.contains("requestTime")) {
            return req
        } else {
            throw InternalErrorException("Error occurred while sending 'RejectBizConnection' type of SMS.")
        }
    }

    suspend fun sendIssueOrderSheetReceiptSMS(
        to: String,
        wholesalerName: String,
        orderNo: String,
        url: String
    ): MessageReqDto {
        val client = createClient()
        val req = MessageReqDto(
            to = to,
            messageType = MessageType.IssueOrderSheetReceipt.toString(),
            args = listOf(wholesalerName, orderNo, url)
        )
        val res = client.post().body(BodyInserters.fromValue(req)).awaitExchange {
            it.awaitBody<String>()
        }

        if (res.contains("requestId") || res.contains("requestTime")) {
            return req
        } else {
            throw InternalErrorException("Error occurred while sending 'IssueOrderSheetReceipt' type of SMS.")
        }
    }


}