package kr.co.marketbill.marketbillcoreserver.datafetcher

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.graphql.datafetcher.OrderFetcher
import kr.co.marketbill.marketbillcoreserver.graphql.scalars.DateTimeScalarType
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.service.CartService
import kr.co.marketbill.marketbillcoreserver.service.MessagingService
import kr.co.marketbill.marketbillcoreserver.service.OrderService
import kr.co.marketbill.marketbillcoreserver.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest(classes = [DgsAutoConfiguration::class, OrderFetcher::class])
@ActiveProfiles("local")
class OrderFetcherTest {
//
//    @Autowired
//    lateinit var dgsQueryExecutor: DgsQueryExecutor
//
//    @MockBean
//    lateinit var dateTimeScalarType: DateTimeScalarType
//
//    @MockBean
//    lateinit var jwtProvider: JwtProvider
//
//    @MockBean
//    lateinit var cartService: CartService
//
//    @MockBean
//    lateinit var messagingService: MessagingService
//
//    @MockBean
//    lateinit var userService: UserService
//
//    @MockBean
//    lateinit var orderService: OrderService
//
//
//    @BeforeEach
//    fun before() {
//        Mockito.`when`(orderService.getOrderItems(null, null, null, PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)))
//            .thenAnswer {
//                val list = listOf<OrderItem>(
//                    OrderItem(
//                        id = 1,
//                        retailer = User(id = 1, name = "reatiler1"),
//                        wholesaler = User(id = 2, name = "wholesaler2")
//                    ),
//                    OrderItem(
//                        id = 2,
//                        retailer = User(id = 1, name = "reatiler1"),
//                        wholesaler = User(id = 2, name = "wholesaler2")
//                    ),
//                )
//                val page: Page<OrderItem> = PageImpl(list)
//                page
//            }
//    }
//
//    @Test
//    fun getOrderSheet() {
//        val orderItemIds: List<Long> = dgsQueryExecutor.executeAndExtractJsonPath(
//            """
//                    {
//                        getOrderItems{
//                            id
//                            retailer {
//                                id
//                                name
//                            }
//                            wholesaler {
//                                id
//                                name
//                           }
//                        }
//                    }
//                """.trimIndent(), "data.getOrderItems[*].id"
//        )
//
//        assert(orderItemIds.size == 2)
//    }
}