package kr.co.marketbill.marketbillcoreserver.user.application.result

data class AuthTokenResult(val accessToken: String, val refreshToken: String) {
    companion object {
        fun from(accessToken: String, refreshToken: String): AuthTokenResult {
            return AuthTokenResult(accessToken = accessToken, refreshToken = refreshToken)
        }
    }
}
