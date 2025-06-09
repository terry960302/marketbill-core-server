package kr.co.marketbill.marketbillcoreserver.shared.constants

import org.springframework.http.HttpStatus

sealed class CustomErrorCode(val status: HttpStatus, val code: String, val message: String) {
        /** Common */
        object SMS_NOT_REACHED :
                CustomErrorCode(
                        status = HttpStatus.INTERNAL_SERVER_ERROR,
                        code = "E001",
                        message = "SMS 발송 중 오류가 발생했습니다."
                )
        object INVALID_FORMAT :
                CustomErrorCode(
                        status = HttpStatus.BAD_REQUEST,
                        code = "E002",
                        message = "잘못된 형식의 데이터입니다."
                )
        object INVALID_DATA :
                CustomErrorCode(
                        status = HttpStatus.BAD_REQUEST,
                        code = "E003",
                        message = "잘못된 데이터입니다."
                )

        /** User */
        object NO_USER :
                CustomErrorCode(
                        status = HttpStatus.NOT_FOUND,
                        code = "E101",
                        message = "사용자를 찾을 수 없습니다."
                )
        object NO_BUSINESS_INFO :
                CustomErrorCode(
                        status = HttpStatus.NOT_FOUND,
                        code = "E102",
                        message = "사업자 정보를 찾을 수 없습니다."
                )
        object TOKEN_NEEDED :
                CustomErrorCode(
                        status = HttpStatus.UNAUTHORIZED,
                        code = "E103",
                        message = "인증이 필요합니다."
                )
        object EMPLOYER_SIGNUP_NEEDED :
                CustomErrorCode(
                        status = HttpStatus.BAD_REQUEST,
                        code = "E104",
                        message = "사장님 회원가입이 필요합니다."
                )
        object NO_WHOLESALE_CONNECTION :
                CustomErrorCode(
                        status = HttpStatus.BAD_REQUEST,
                        code = "E105",
                        message = "도매상과의 거래 관계가 없습니다."
                )
        object BIZ_CONNECTION_DUPLICATED :
                CustomErrorCode(
                        status = HttpStatus.CONFLICT,
                        code = "E106",
                        message = "이미 거래처 관계가 존재합니다."
                )
        object NO_BIZ_CONNECTION :
                CustomErrorCode(
                        status = HttpStatus.NOT_FOUND,
                        code = "E107",
                        message = "거래처 정보를 찾을 수 없습니다."
                )
        object PHONE_NO_DUPLICATED :
                CustomErrorCode(
                        status = HttpStatus.CONFLICT,
                        code = "E108",
                        message = "이미 등록된 전화번호입니다."
                )
        object USER_NAME_DUPLICATED :
                CustomErrorCode(
                        status = HttpStatus.CONFLICT,
                        code = "E109",
                        message = "이미 사용 중인 이름입니다."
                )

        /** Order */
        object NO_SHOPPING_SESSION :
                CustomErrorCode(
                        status = HttpStatus.NOT_FOUND,
                        code = "E201",
                        message = "쇼핑 세션을 찾을 수 없습니다."
                )
        object NO_CART_ITEM :
                CustomErrorCode(
                        status = HttpStatus.NOT_FOUND,
                        code = "E202",
                        message = "장바구니 아이템을 찾을 수 없습니다."
                )
        object NO_CART_WHOLESALER :
                CustomErrorCode(
                        status = HttpStatus.BAD_REQUEST,
                        code = "E203",
                        message = "장바구니에 도매상이 연결되어 있지 않습니다."
                )
        object NO_ORDER_SHEET :
                CustomErrorCode(
                        status = HttpStatus.NOT_FOUND,
                        code = "E204",
                        message = "주문서를 찾을 수 없습니다."
                )
        object NO_PRICE_ORDER_ITEM :
                CustomErrorCode(
                        status = HttpStatus.BAD_REQUEST,
                        code = "E205",
                        message = "주문 항목에 가격 정보가 없습니다."
                )

        /** New */
        object NO_FLOWER :
                CustomErrorCode(
                        status = HttpStatus.NOT_FOUND,
                        code = "E301",
                        message = "꽃을 찾을 수 없습니다."
                )
}
