package kr.co.marketbill.marketbillcoreserver.datafetcher

//import com.netflix.graphql.dgs.DgsQueryExecutor
//import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
//import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
//import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
//import kr.co.marketbill.marketbillcoreserver.domain.dto.GetFlowersOutput
//import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
//import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.FlowerType
//import kr.co.marketbill.marketbillcoreserver.graphql.datafetcher.FlowerFetcher
//import kr.co.marketbill.marketbillcoreserver.graphql.scalars.DateTimeScalarType
//import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
//import kr.co.marketbill.marketbillcoreserver.service.FlowerService
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.mock.mockito.MockBean
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.PageImpl
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Sort
//import org.springframework.test.context.ActiveProfiles
//import java.time.LocalDate
//
//@SpringBootTest(classes = [DgsAutoConfiguration::class, FlowerFetcher::class])
//@ActiveProfiles("local")
//class FlowerFetcherTest {
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
//    lateinit var flowerService: FlowerService
//
//    @BeforeEach
//    fun before() {
//        Mockito.`when`(
//            flowerService.getFlowers(
//                LocalDate.parse("2022-09-22"),
//                LocalDate.parse("2022-11-30"),
//                null,
//                PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
//            )
//        )
//            .thenAnswer {
//                val flowerType = FlowerType(
//                    id = 1,
//                    name = "장미"
//                )
//                val flower1 = Flower(
//                    id = 1,
//                    name = "세렝게티의 그냥꽃",
//                    flowerType = flowerType,
//                )
//                val flower2 = Flower(
//                    id = 1,
//                    name = "세렝게티의 특별한 꽃",
//                    flowerType = flowerType,
//                )
//                val page: Page<Flower> = PageImpl(listOf(flower1, flower2))
//                GetFlowersOutput(
//                    resultCount = page.totalElements,
//                    flowers = page,
//                )
//            }
//    }
//
//    @Test
//    // 사입할 수 있는 꽃 목록
//    fun getBuyableFlowers() {
//        val flowers: List<Long> = dgsQueryExecutor.executeAndExtractJsonPath(
//            """
//                    query {
//                        getFlowers(filter :{
//                            dateRange : {
//                              fromDate: "2022-09-22",
//                              toDate : "2022-11-30"
//                            }
//                          }){
//                            resultCount
//                            flowers{
//                                id
//                                name
//                            }
//                          }
//                    }
//                """.trimIndent(), "data.getFlowers.flowers[*].id"
//        )
//
//        assert(flowers.size == 2)
//    }
//
//
//}