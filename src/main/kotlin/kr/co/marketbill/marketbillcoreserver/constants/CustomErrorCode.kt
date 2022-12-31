package kr.co.marketbill.marketbillcoreserver.constants

enum class CustomErrorCode {
    /**
     * Common
     */
    SMS_NOT_REACHED, // SMS 발송하는데 오류 발생
    INVALID_FORMAT, // 잘못된 양식의 데이터
    /**
     * User
     */
    NO_USER, // id에 해당하는 user 데이터가 없음
    NO_BUSINESS_INFO, // userId에 해당하는 business info 데이터가 없음
    TOKEN_NEEDED, // 토큰이 필요
    EMPLOYER_SIGNUP_NEEDED, // 사장님 회원가입이 먼저 필요
    NO_WHOLESALE_CONNECTION, // 도매상 관계가 없어서 처리 불가
    BIZ_CONNECTION_DUPLICATED, // 이미 거래처 관계가 있음
    NO_BIZ_CONNECTION, // 거래처 데이터가 없어서 처리 불가
    PHONE_NO_DUPLICATED, // 가입시 동일한 전화번호가 있음.
    USER_NAME_DUPLICATED, // 유저 이름 중복
    /**
     * Order
     */
    NO_CART_ITEM, // id에 해당하는 cart_item 데이터가 없음.(혹은 처리에 필요한 장바구니 데이터가 없음.)
    NO_CART_WHOLESALER, // 장바구니에 도매상이 연결되어 있지 않음.
    NO_ORDER_SHEET, // id에 해당하는 orderSheet 데이터가 없음.
    NO_PRICE_ORDER_ITEM, // 주문항목에 가격 데이터가 없음.
}