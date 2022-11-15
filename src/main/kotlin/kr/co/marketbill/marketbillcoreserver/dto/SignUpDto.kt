package kr.co.marketbill.marketbillcoreserver.dto

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole


data class SignUpDto(
    val name: String,
    val phoneNo: String,
    val password: String,
    val role: AccountRole,
) {

}


//abstract class SignUpDto {
//    abstract val name: String
//    abstract val phoneNo: String
//    abstract val password: String
//}
//
//data class SignUpRetailerDto(
//    override val name: String,
//    override val phoneNo: String,
//    override val password: String
//) : SignUpDto() {
//
//}
//
//data class SignUpWholesalerDto(
//    override val name: String,
//    override val phoneNo: String,
//    override val password: String,
//    val wholesalerType: String,
//) : SignUpDto() {
//
//}