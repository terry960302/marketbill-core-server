package kr.co.marketbill.marketbillcoreserver.cart

import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.datafetcher.CartFieldFetcher
import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.dataloader.CartItemsDataLoader
import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.mapper.CartOutputMapper
import kr.co.marketbill.marketbillcoreserver.cart.application.service.CartService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class CartIntegrationTest {

    @Test
    fun `Cart 관련 컴포넌트들이 정상적으로 로드된다`() {
        // 이 테스트는 Cart 관련 컴포넌트들이 Spring Context에 정상적으로 로드되는지 확인합니다.
        // 실제 동작은 GraphQL Playground에서 테스트합니다.
    }
} 