package kr.co.marketbill.marketbillcoreserver.service

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.data.entity.flower.BiddingFlower
import kr.co.marketbill.marketbillcoreserver.data.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.data.entity.flower.FlowerType
import kr.co.marketbill.marketbillcoreserver.data.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.data.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.data.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.data.entity.user.AuthToken
import kr.co.marketbill.marketbillcoreserver.data.entity.user.BizConnection
import kr.co.marketbill.marketbillcoreserver.data.entity.user.User
import kr.co.marketbill.marketbillcoreserver.data.entity.user.UserCredential
import kr.co.marketbill.marketbillcoreserver.data.repository.flower.BiddingFlowerRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.flower.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.flower.FlowerTypeRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.order.CartRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.order.OrderItemRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.order.OrderSheetRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.user.AuthTokenRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.user.BizConnectionRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.user.UserCredentialRepository
import kr.co.marketbill.marketbillcoreserver.data.repository.user.UserRepository
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.util.EnumConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

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

    val logger: Logger = LoggerFactory.getLogger(MockService::class.java)

    @Profile("local", "dev")
    @PostConstruct
    fun createAllMockToDB() {
        createMockUsers()
        createMockFlowers()

        // 소매상
        createMockCartItems()
        createMockOrderSheets()
    }

    @Transactional
    fun createMockUsers() {
        val retailer = User(name = "name_retailer", businessNo = null)
        val wholesaler = User(name = "name_wholesaler", businessNo = null)
        userRepository.saveAll(arrayListOf(retailer, wholesaler))

        val retailerCred = UserCredential(
            user = entityManager.getReference(User::class.java, 1.toLong()),
            phoneNo = "01011112222",
            password = passwordEncoder.encode("1234"),
            role = AccountRole.ROLE_RETAILER
        )
        val wholesalerCred = UserCredential(
            user = entityManager.getReference(User::class.java, 2.toLong()),
            phoneNo = "01011113333",
            password = passwordEncoder.encode("1234"),
            role = AccountRole.ROLE_WHOLESALER_EMPR
        )
        userCredentialRepository.saveAll(arrayListOf(retailerCred, wholesalerCred))

        val authToken1 = AuthToken(
            user = entityManager.getReference(User::class.java, 1.toLong()),
            refreshToken = jwtProvider.generateToken(
                1.toLong(),
                AccountRole.ROLE_RETAILER.toString(),
                JwtProvider.refreshExpiration
            )
        )
        val authToken2 = AuthToken(
            user = entityManager.getReference(User::class.java, 2.toLong()),
            refreshToken = jwtProvider.generateToken(
                2.toLong(),
                AccountRole.ROLE_RETAILER.toString(),
                JwtProvider.refreshExpiration
            )
        )
        authTokenRepository.saveAll(arrayListOf(authToken1, authToken2))

        val bizConn = BizConnection(
            retailer = entityManager.getReference(User::class.java, 1.toLong()),
            wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
            applyStatus = ApplyStatus.APPLYING,
        )
        bizConnectionRepository.save(bizConn)

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

    @Transactional
    fun createMockOrderSheets() {

        val orderSheets = (1..3).map {
            OrderSheet(
                orderNo= UUID.randomUUID().toString(),
                retailer = entityManager.getReference(User::class.java, 1.toLong()),
                wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
            )
        }
        orderSheetRepository.saveAll(orderSheets)

        val orderItems = (1..9).map {
            val orderSheetId = ((it -1) / 3) + 1
            OrderItem(
                orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId.toLong()),
                retailer = entityManager.getReference(User::class.java, 1.toLong()),
                wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
                flower = entityManager.getReference(Flower::class.java, it.toLong()),
                quantity = (1..100).random(),
                grade = EnumConverter.convertFlowerGradeToKor(FlowerGrade.UPPER)
            )
        }
        orderItemRepository.saveAll(orderItems)
    }
}