package kr.co.marketbill.marketbillcoreserver.legacy.application.dto.response

data class AuthTokenDto(
    val accessToken : String,
    val refreshToken : String,
) {
}