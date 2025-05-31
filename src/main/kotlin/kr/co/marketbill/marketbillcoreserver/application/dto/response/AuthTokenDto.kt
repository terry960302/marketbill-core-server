package kr.co.marketbill.marketbillcoreserver.application.dto.response

data class AuthTokenDto(
    val accessToken : String,
    val refreshToken : String,
) {
}