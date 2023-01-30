package kr.co.marketbill.marketbillcoreserver.constants

enum class AccountRole {
    RETAILER, // 소매상
    WHOLESALER_EMPR, // 도매상 고용주(EMPLOYER)
    WHOLESALER_EMPE  // 도매상 고용자(EMPLOYEE)
}

enum class ApplyStatus {
    APPLYING,
    CONFIRMED,
    REJECTED,
}

enum class FlowerGrade {
    UPPER,
    MIDDLE,
    LOWER,
}

enum class MessageTemplate {
    Default,
    Verification,
    ApplyBizConnection,
    ConfirmBizConnection,
    RejectBizConnection,
    IssueOrderSheetReceipt,
}

// string -> enum
inline fun <reified T : Enum<T>, V> ((T) -> V).find(value: V): T? {
    return enumValues<T>().firstOrNull { this(it) == value }
}
//// enum -> string
//inline fun <reified T : Enum<T>, V> ((T) -> V).toString(value: V): T? {
//    return enumValues<T>().firstOrNull { this(it) == value }
//}