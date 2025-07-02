package kr.co.marketbill.marketbillcoreserver.legacy.domain.validator

import java.net.URL

object UrlValidator {
    fun isValid(urlString: String?): Boolean {
        return try {
            URL(urlString)
            true
        } catch (_: Exception) {
            false
        }
    }
}
