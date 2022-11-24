package kr.co.marketbill.marketbillcoreserver.domain.dto

data class AuthTokenDto(
    val accessToken : String,
    val refreshToken : String,
) {
}