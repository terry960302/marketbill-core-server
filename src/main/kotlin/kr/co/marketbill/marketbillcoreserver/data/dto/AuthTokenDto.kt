package kr.co.marketbill.marketbillcoreserver.data.dto

data class AuthTokenDto(
    val accessToken : String,
    val refreshToken : String,
) {
}