package kr.co.marketbill.marketbillcoreserver.dto

data class AuthTokenDto(
    val accessToken : String,
    val refreshToken : String,
) {
}