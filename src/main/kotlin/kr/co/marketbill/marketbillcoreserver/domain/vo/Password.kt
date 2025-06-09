package kr.co.marketbill.marketbillcoreserver.domain.vo

import kr.co.marketbill.marketbillcoreserver.domain.validator.PasswordValidator

data class Password(val value: String) {
    init {
        require(PasswordValidator.isValid(value)) { "Invalid password format" }
    }
}
