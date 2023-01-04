package kr.co.marketbill.marketbillcoreserver.util

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kr.co.marketbill.marketbillcoreserver.service.UserService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.regex.Pattern

@SpringBootTest(classes = [UserService::class])
@ActiveProfiles("local")
class PasswordValidatorTest : ShouldSpec() {
    private lateinit var validatePassword: (String) -> Boolean

    init {
        beforeTest {
            validatePassword = fun(password: String): Boolean {
                // 영문, 숫자, 특수문자 조합 8자 이상
                val minLength = 8

                val passwordPattern = "^(?=.*[0-9])(?=.*[@#\$%^&+=])(?=\\\\S+\$).{${minLength},}\$"
                val pattern = Pattern.compile(passwordPattern)
                val matcher = pattern.matcher(password)

                return matcher.matches()
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