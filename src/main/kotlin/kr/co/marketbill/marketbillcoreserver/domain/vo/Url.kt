package kr.co.marketbill.marketbillcoreserver.domain.vo

import kr.co.marketbill.marketbillcoreserver.domain.validator.UrlValidator

data class Url(val value: String?) {
    init {
        require(UrlValidator.isValid(value)) { "Invalid url format" }
    }
}
