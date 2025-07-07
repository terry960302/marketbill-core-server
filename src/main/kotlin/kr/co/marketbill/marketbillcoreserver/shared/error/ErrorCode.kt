package kr.co.marketbill.marketbillcoreserver.shared.error

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: Int, val code: String, val message: String) {
    // Common
    SMS_NOT_REACHED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "E001", "SMS 발송 중 오류가 발생했습니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST.value(), "E002", "잘못된 형식의 데이터입니다."),
    INVALID_DATA(HttpStatus.BAD_REQUEST.value(), "E003", "잘못된 데이터입니다."),
    // User
    NO_USER(HttpStatus.NOT_FOUND.value(), "E101", "사용자를 찾을 수 없습니다."),
    NO_BUSINESS_INFO(HttpStatus.NOT_FOUND.value(), "E102", "사업자 정보를 찾을 수 없습니다."),
    TOKEN_NEEDED(HttpStatus.UNAUTHORIZED.value(), "E103", "인증이 필요합니다."),
    EMPLOYER_SIGNUP_NEEDED(HttpStatus.BAD_REQUEST.value(), "E104", "사장님 회원가입이 필요합니다."),
    NO_WHOLESALE_CONNECTION(HttpStatus.BAD_REQUEST.value(), "E105", "도매상과의 거래 관계가 없습니다."),
    BIZ_CONNECTION_DUPLICATED(HttpStatus.CONFLICT.value(), "E106", "이미 거래처 관계가 존재합니다."),
    NO_BIZ_CONNECTION(HttpStatus.NOT_FOUND.value(), "E107", "거래처 정보를 찾을 수 없습니다."),
    PHONE_NO_DUPLICATED(HttpStatus.CONFLICT.value(), "E108", "이미 등록된 전화번호입니다."),
    USER_NAME_DUPLICATED(HttpStatus.CONFLICT.value(), "E109", "이미 사용 중인 이름입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST.value(), "E110", "잘못된 역할입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "E111", "잘못된 비밀번호입니다."),
    DELETED_USER(HttpStatus.NOT_FOUND.value(), "E112", "삭제된 사용자입니다."),
    
    // Order
    NO_SHOPPING_SESSION(HttpStatus.NOT_FOUND.value(), "E201", "쇼핑 세션을 찾을 수 없습니다."),
    NO_CART_ITEM(HttpStatus.NOT_FOUND.value(), "E202", "장바구니 아이템을 찾을 수 없습니다."),
    NO_CART_WHOLESALER(HttpStatus.BAD_REQUEST.value(), "E203", "장바구니에 도매상이 연결되어 있지 않습니다."),
    NO_ORDER_SHEET(HttpStatus.NOT_FOUND.value(), "E204", "주문서를 찾을 수 없습니다."),
    NO_PRICE_ORDER_ITEM(HttpStatus.BAD_REQUEST.value(), "E205", "주문 항목에 가격 정보가 없습니다."),
    // Cart
    CART_ITEM_ALREADY_DELETED(HttpStatus.BAD_REQUEST.value(), "E206", "이미 삭제된 장바구니 항목입니다."),
    CART_ITEM_ALREADY_ORDERED(HttpStatus.BAD_REQUEST.value(), "E207", "이미 주문된 장바구니 항목은 삭제할 수 없습니다."),
    CART_ITEM_ALREADY_ORDERED_UPDATE(HttpStatus.BAD_REQUEST.value(), "E208", "이미 주문된 장바구니 항목입니다."),
    NO_RETAILER(HttpStatus.NOT_FOUND.value(), "E209", "소매상을 찾을 수 없습니다."),
    NO_WHOLESALER(HttpStatus.NOT_FOUND.value(), "E210", "도매상을 찾을 수 없습니다."),
    // New
    NO_FLOWER(HttpStatus.NOT_FOUND.value(), "E301", "꽃을 찾을 수 없습니다."),
    // Order
    CANNOT_DELETE_ORDER_SHEET_WITH_RECEIPT(HttpStatus.BAD_REQUEST.value(), "E302", "영수증이 발행된 주문서는 삭제할 수 없습니다."),
    NO_ORDER_ITEM(HttpStatus.NOT_FOUND.value(), "E303", "주문 항목을 찾을 수 없습니다."),
    NO_CUSTOM_ORDER_ITEM(HttpStatus.NOT_FOUND.value(), "E304", "커스텀 주문 항목을 찾을 수 없습니다."),
    NO_DAILY_ORDER_ITEM(HttpStatus.NOT_FOUND.value(), "E305", "일별 주문 항목을 찾을 수 없습니다."),
    NO_ORDER_SHEET_RECEIPT(HttpStatus.NOT_FOUND.value(), "E306", "주문서 영수증을 찾을 수 없습니다.");
}
