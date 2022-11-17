package kr.co.marketbill.marketbillcoreserver.constants

enum class AccountRole {
    ROLE_ADMIN, // 관리자
    ROLE_RETAILER, // 소매상
    ROLE_WHOLESALER_EMPR, // 도매상 고용주(EMPLOYER)
    ROLE_WHOLESALER_EMPE  // 도매상 고용자(EMPLOYEE)
}

enum class ApplyStatus {
    APPLYING,
    CONFIRMED,
    REJECTED,
}

// string -> enum
inline fun <reified T : Enum<T>, V> ((T) -> V).find(value: V): T? {
    return enumValues<T>().firstOrNull { this(it) == value }
}
//// enum -> string
//inline fun <reified T : Enum<T>, V> ((T) -> V).toString(value: V): T? {
//    return enumValues<T>().firstOrNull { this(it) == value }
//}