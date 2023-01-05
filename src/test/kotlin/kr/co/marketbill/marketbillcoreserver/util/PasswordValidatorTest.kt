package kr.co.marketbill.marketbillcoreserver.util

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class PasswordValidatorTest : ShouldSpec() {
    private lateinit var validatePassword: (String) -> Boolean

    init {
        beforeTest {
            validatePassword = fun(password: String): Boolean {
                // 영문, 숫자, 특수문자 조합 + 공백없음
                val minLength = 8
                val passwordPattern = "^(?!.* )(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#\$%^&*]).{$minLength,}\$".toRegex()
                return password.matches(passwordPattern)
            }
        }

        should("return TRUE") {
            validatePassword("test123@@") shouldBe true
            validatePassword("TEST123^^") shouldBe true
        }

        should("return FALSE") {
            validatePassword("test123") shouldBe false
            validatePassword("^asksSde") shouldBe false
            validatePassword("12") shouldBe false
        }
    }
}