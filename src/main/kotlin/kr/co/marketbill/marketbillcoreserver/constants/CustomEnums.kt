package kr.co.marketbill.marketbillcoreserver.constants

enum class AccountRole {
    RETAILER,
    WHOLESALER_EMPR, // 고용주(EMPLOYER)
    WHOLESALER_EMPE  // 고용자(EMPLOYEE)
}

// string -> enum
inline fun <reified T : Enum<T>, V> ((T) -> V).find(value: V): T? {
    return enumValues<T>().firstOrNull { this(it) == value }
}
//// enum -> string
//inline fun <reified T : Enum<T>, V> ((T) -> V).toString(value: V): T? {
//    return enumValues<T>().firstOrNull { this(it) == value }
//}