package kr.co.marketbill.marketbillcoreserver.legacy.domain.validator

object PasswordValidator {
    private const val MIN_LENGTH = 8
    private val PASSWORD_REGEX = "^(?!.* )(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#\\$%^&*]).{$MIN_LENGTH,}\$".toRegex()

    fun isValid(password: String): Boolean {
        return password.matches(PASSWORD_REGEX)
    }
}
