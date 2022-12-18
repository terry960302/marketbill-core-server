package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.FlowerType
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.*
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.BiddingFlowerRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.flower.FlowerTypeRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.CartRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.domain.repository.user.*
import kr.co.marketbill.marketbillcoreserver.domain.specs.BizConnSpecs
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter
import kr.co.marketbill.marketbillcoreserver.util.StringGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import kotlin.streams.asSequence

@Service
class MockService {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userCredentialRepository: UserCredentialRepository

    @Autowired
    private lateinit var authTokenRepository: AuthTokenRepository

    @Autowired
    private lateinit var bizConnectionRepository: BizConnectionRepository

    @Autowired
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @Autowired
    private lateinit var flowerTypeRepository: FlowerTypeRepository

    @Autowired
    private lateinit var biddingFlowerRepository: BiddingFlowerRepository

    @Autowired
    private lateinit var flowerRepository: FlowerRepository

    @Autowired
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var orderSheetRepository: OrderSheetRepository

    @Autowired
    private lateinit var wholesalerConnectionRepository: WholesalerConnectionRepository

    val logger: Logger = LoggerFactory.getLogger(MockService::class.java)

    @Profile("local", "dev")
    @PostConstruct
    fun createAllMockToDB() {
        createMockFlowers()
        createMockUsers()
        createMockCartItems()
        createMockOrderSheets()
    }

    @Transactional
    fun createMockOnlyUsers(fromCount: Int = 1, toCount: Int = 100, role: AccountRole) {
        val belongsTo = when(role){
            AccountRole.RETAILER -> null
            AccountRole.WHOLESALER_EMPR -> "양재"
            AccountRole.WHOLESALER_EMPE -> "양재"
        }

        val users = (fromCount..toCount).map {
            User(
                id = it.toLong(),
                name = generateRandomStr(),
                businessNo = null,
                belongsTo=belongsTo
            )
        }


        val creds = (fromCount..toCount).map {
            UserCredential(
                user = entityManager.getReference(User::class.java, it.toLong()),
                phoneNo = generatePhoneNoStr(),
                password = passwordEncoder.encode("1234"),
                role = role,
            )
        }

        val authTokens = (fromCount..toCount).map {
            AuthToken(
                user = entityManager.getReference(User::class.java, it.toLong()),
                refreshToken = jwtProvider.generateToken(
                    it.toLong(),
                    role.toString(),
                    JwtProvider.REFRESH_EXPIRATION
                )
            )
        }

        userRepository.saveAll(users)
        userCredentialRepository.saveAll(creds)
        authTokenRepository.saveAll(authTokens)
    }

    @Transactional
    fun createMockBizConns() {
        val conns = (1..3).map {
            BizConnection(
                retailer = entityManager.getReference(User::class.java, (1..1).random().toLong()),
                wholesaler = entityManager.getReference(User::class.java, (2..3).random().toLong()),
                applyStatus = ApplyStatus.APPLYING,
            )
        }
        bizConnectionRepository.saveAll(conns)
    }

    @Transactional
    fun createWholesalerConns(employerId : Long, employeeIds : List<Long>){
        val conns = employeeIds.map {
            WholesalerConnection(
                employer = entityManager.getReference(User::class.java, employerId),
                employee = entityManager.getReference(User::class.java, it)
            )
        }
        wholesalerConnectionRepository.saveAll(conns)

    }

    @Transactional
    fun createMockUsers() {
        createMockOnlyUsers(1,1, AccountRole.RETAILER)
        createMockOnlyUsers(2, 2, AccountRole.WHOLESALER_EMPR)
        createMockOnlyUsers(3, 4, AccountRole.WHOLESALER_EMPE)
        createWholesalerConns(employerId = 2, employeeIds = listOf(3, 4))
        createMockBizConns()

        logger.trace("createMockUsers completed")
    }

    @Transactional
    fun createMockFlowers() {
        val flowerType1 = FlowerType(1, "국화")
        val flowerType2 = FlowerType(2, "과꽃")
        flowerTypeRepository.saveAll(mutableListOf(flowerType1, flowerType2))

        val flowerNames1 = arrayListOf<String>("테데오옐로우", "신명", "상그릴라", "설악", "백장미소국", "이소국(옐로우)", "대국", "필링다크")
        val flowerNames2 = arrayListOf<String>(
            "코코토(미니)",
            "씨스타화이트",
            "코코토화이트(미니)",
            "레지스트",
            "아즈미화이트",
            "라이트블루",
            "코코토핑크(미니)",
            "송본화이트",
            "미니과꽃(로즈화이트)",
            "옐로우"
        )

        val flowers1 = flowerNames1.map { name ->
            Flower(flowerType = entityManager.getReference(FlowerType::class.java, 1.toLong()), name = name)
        }
        val flowers2 = flowerNames2.map { name ->
            Flower(flowerType = entityManager.getReference(FlowerType::class.java, 2.toLong()), name = name)
        }
        val flowers = flowerRepository.saveAll(flowers1 + flowers2)


        val biddingFlowers = flowers.map {
            BiddingFlower(flower = it, biddingDate = LocalDateTime.now())
        }
        biddingFlowerRepository.saveAll(biddingFlowers)

        logger.trace("createMockFlowers completed")
    }

    @Transactional
    fun createMockCartItems() {
        val cartItems = (1..5).map {
            CartItem(
                retailer = entityManager.getReference(User::class.java, 1.toLong()),
                flower = entityManager.getReference(Flower::class.java, it.toLong()),
                quantity = 10,
                grade = EnumConverter.convertFlowerGradeToKor(FlowerGrade.UPPER)
            )
        }
        cartRepository.saveAll(cartItems)
        logger.trace("createMockCartItems completed")
    }

    // 3일정도 주기의 주문서
    @Transactional
    fun createMockOrderSheets() {

        val orderSheets = (1..3).map {
            OrderSheet(
                orderNo = StringGenerator.generateOrderNo(it.toLong()),
                retailer = entityManager.getReference(User::class.java, 1.toLong()),
                wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
            )
        }
        orderSheetRepository.saveAll(orderSheets)

        val orderItems = (1..9).map {
            val orderSheetId = ((it - 1) / 3) + 1
            OrderItem(
                orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId.toLong()),
                retailer = entityManager.getReference(User::class.java, 1.toLong()),
                wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
                flower = entityManager.getReference(Flower::class.java, it.toLong()),
                quantity = (1..100).random(),
                grade = EnumConverter.convertFlowerGradeToKor(FlowerGrade.UPPER),
                price = (100..10000).random()
            )
        }
        orderItemRepository.saveAll(orderItems)
    }

    fun generateRandomStr(outputStrLength: Long = 10): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return java.util.Random().ints(outputStrLength, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
    }

    fun generatePhoneNoStr(outputStrLength: Long = 8): String {
        val source = "0123456789"
        val postNo = Random().ints(outputStrLength, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
        return "010$postNo"
    }
}