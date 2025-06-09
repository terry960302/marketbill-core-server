package kr.co.marketbill.marketbillcoreserver.util

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kr.co.marketbill.marketbillcoreserver.domain.validator.UrlValidator

class UrlValidatorTest : ShouldSpec({
    should("return TRUE") {
        UrlValidator.isValid("https://example.com") shouldBe true
        UrlValidator.isValid("http://example.com") shouldBe true
    }

    should("return FALSE") {
        UrlValidator.isValid("htp:/wrong") shouldBe false
        UrlValidator.isValid("") shouldBe false
    }
})
