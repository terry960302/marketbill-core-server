package kr.co.marketbill.marketbillcoreserver.util

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kr.co.marketbill.marketbillcoreserver.domain.validator.PasswordValidator

class PasswordValidatorTest : ShouldSpec({
    should("return TRUE") {
        PasswordValidator.isValid("test123@@") shouldBe true
        PasswordValidator.isValid("TEST123^^") shouldBe true
    }

    should("return FALSE") {
        PasswordValidator.isValid("test123") shouldBe false
        PasswordValidator.isValid("^asksSde") shouldBe false
        PasswordValidator.isValid("12") shouldBe false
    }
})
