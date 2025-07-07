package kr.co.marketbill.marketbillcoreserver.shared.config;


import org.springframework.context.annotation.Configuration;

import java.util.*
import kotlin.streams.asSequence
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import javax.persistence.EntityManager



@Profile("local")
@Configuration
class DummyDataLoader {
//    @Autowired
//    private lateinit var entityManager: EntityManager
//
//    @Autowired
//    private lateinit var userRepository: UserRepository
//
//    @Autowired
//    private lateinit var userCredentialRepository: UserCredentialRepository
//
//    @Autowired
//    private lateinit var authTokenRepository: AuthTokenRepository
//
//    @Autowired
//    private lateinit var bizConnectionRepository: BizConnectionRepository
//
//    @Autowired
//    private lateinit var passwordEncoder: BCryptPasswordEncoder
//
//    @Autowired
//    private lateinit var jwtProvider: JwtProvider
//
//    @Autowired
//    private lateinit var flowerTypeRepository: FlowerTypeRepository
//
//    @Autowired
//    private lateinit var biddingFlowerRepository: BiddingFlowerRepository
//
//    @Autowired
//    private lateinit var flowerRepository: FlowerRepository
//
//    @Autowired
//    private lateinit var cartItemRepository: CartItemRepository
//
//    @Autowired
//    private lateinit var orderItemRepository: OrderItemRepository
//
//    @Autowired
//    private lateinit var dailyOrderItemRepository: DailyOrderItemRepository
//
//    @Autowired
//    private lateinit var orderSheetRepository: OrderSheetRepository
//
//    @Autowired
//    private lateinit var wholesalerConnectionRepository: WholesalerConnectionRepository
//
//    val logger: Logger = LoggerFactory.getLogger(DummyDataLoader::class.java)
//
//    @Value("\${spring.config.activate.on-profile}")
//    private lateinit var profile: String
//
//
//    fun createAllMockToDB() {
//        if (profile == "local") {
////            createMockFlowers()
////            createMockUsers()
////            createMockCartItems()
////            createMockOrderSheets()
////            deleteDuplicateFlowers()
//        }
//    }
//
//    @Transactional
//    fun deleteDuplicateFlowers() {
//        val flowerIdsToDelete = flowerRepository.findAll().groupBy {
//            val key = "${it.flowerType!!.name}-${it.name}"
//            key
//        }.mapValues {
//            it.value.map { it.id }
//        }.filterValues { it.size > 1 }
//            .mapValues { it.value.subList(1, it.value.size) }.flatMap { it.value }
//
//        flowerRepository.deleteAllById(flowerIdsToDelete)
//        // [628, 646, 664, 682, 700, 718, 736, 754, 772, 790, 808, 826, 844, 862, 629, 647, 665, 683, 701, 719, 737, 755, 773, 791, 809, 827, 845, 863, 630, 648, 666, 684, 702, 720, 738, 756, 774, 792, 810, 828, 846, 864, 631, 649, 667, 685, 703, 721, 739, 757, 775, 793, 811, 829, 847, 865, 632, 650, 668, 686, 704, 722, 740, 758, 776, 794, 812, 830, 848, 866, 633, 651, 669, 687, 705, 723, 741, 759, 777, 795, 813, 831, 849, 867, 634, 652, 670, 688, 706, 724, 742, 760, 778, 796, 814, 832, 850, 868, 635, 653, 671, 689, 707, 725, 743, 761, 779, 797, 815, 833, 851, 869, 636, 654, 672, 690, 708, 726, 744, 762, 780, 798, 816, 834, 852, 870, 637, 655, 673, 691, 709, 727, 745, 763, 781, 799, 817, 835, 853, 871, 638, 656, 674, 692, 710, 728, 746, 764, 782, 800, 818, 836, 854, 872, 639, 657, 675, 693, 711, 729, 747, 765, 783, 801, 819, 837, 855, 873, 640, 658, 676, 694, 712, 730, 748, 766, 784, 802, 820, 838, 856, 874, 641, 659, 677, 695, 713, 731, 749, 767, 785, 803, 821, 839, 857, 875, 642, 660, 678, 696, 714, 732, 750, 768, 786, 804, 822, 840, 858, 876, 643, 661, 679, 697, 715, 733, 751, 769, 787, 805, 823, 841, 859, 877, 644, 662, 680, 698, 716, 734, 752, 770, 788, 806, 824, 842, 860, 878, 645, 663, 681, 699, 717, 735, 753, 771, 789, 807, 825, 843, 861, 879]
//
//    }
//
//    @Transactional
//    fun createMockOnlyUsers(fromCount: Int = 1, toCount: Int = 100, role: AccountRole) {
//        val belongsTo = when (role) {
//            AccountRole.RETAILER -> null
//            AccountRole.WHOLESALER_EMPR -> "양재"
//            AccountRole.WHOLESALER_EMPE -> "양재"
//        }
//
//        val users = (fromCount..toCount).map {
//            User.builder(
//                id = it.toLong(),
//                name = generateRandomStr(),
//                belongsTo = belongsTo,
//            )
//        }
//
//
//        val creds = (fromCount..toCount).map {
//            UserCredential.create(
//                user = entityManager.getReference(User::class.java, it.toLong()),
//                phoneNo = generatePhoneNoStr(),
//                password = passwordEncoder.encode("1234"),
//                role = role,
//                id = null,
//            )
//        }
//
//        val authTokens = (fromCount..toCount).map {
//            AuthToken.create(
//                user = entityManager.getReference(User::class.java, it.toLong()),
//                refreshToken = jwtProvider.generateToken(
//                    it.toLong(),
//                    role.toString(),
//                    JwtProvider.REFRESH_EXPIRATION
//                )
//            )
//        }
//
//        userRepository.saveAll(users)
//        userCredentialRepository.saveAll(creds)
//        authTokenRepository.saveAll(authTokens)
//    }
//
//    @Transactional
//    fun createMockBizConns() {
//        val conn1 = BizConnection(
//            retailer = entityManager.getReference(User::class.java, 1.toLong()),
//            wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
//            applyStatus = ApplyStatus.APPLYING,
//        )
//        bizConnectionRepository.saveAll(listOf(conn1))
//    }
//
//    @Transactional
//    fun createWholesalerConns(employerId: Long, employeeIds: List<Long>) {
//        val conns = employeeIds.map {
//            WholesalerConnection(
//                employer = entityManager.getReference(User::class.java, employerId),
//                employee = entityManager.getReference(User::class.java, it)
//            )
//        }
//        wholesalerConnectionRepository.saveAll(conns)
//
//    }
//
//    @Transactional
//    fun createMockUsers() {
//        createMockOnlyUsers(1, 1, AccountRole.RETAILER)
//        createMockOnlyUsers(2, 2, AccountRole.WHOLESALER_EMPR)
//        createMockOnlyUsers(3, 4, AccountRole.WHOLESALER_EMPE)
//        createWholesalerConns(employerId = 2, employeeIds = listOf(3, 4))
//        createMockBizConns()
//
//        logger.trace("createMockUsers completed")
//    }
//
//    @Transactional
//    fun createMockFlowers() {
//        val flowerType1 = FlowerType(1, "국화")
//        val flowerType2 = FlowerType(2, "과꽃")
//        flowerTypeRepository.saveAll(mutableListOf(flowerType1, flowerType2))
//
//        val flowerNames1 = arrayListOf<String>("테데오옐로우", "신명", "상그릴라", "설악", "백장미소국", "이소국(옐로우)", "대국", "필링다크")
//        val flowerNames2 = arrayListOf<String>(
//            "코코토(미니)",
//            "씨스타화이트",
//            "코코토화이트(미니)",
//            "레지스트",
//            "아즈미화이트",
//            "라이트블루",
//            "코코토핑크(미니)",
//            "송본화이트",
//            "미니과꽃(로즈화이트)",
//            "옐로우"
//        )
//
//        val flowers1 = flowerNames1.map { name ->
//            Flower(flowerType = entityManager.getReference(FlowerType::class.java, 1.toLong()), name = name)
//        }
//        val flowers2 = flowerNames2.map { name ->
//            Flower(flowerType = entityManager.getReference(FlowerType::class.java, 2.toLong()), name = name)
//        }
//        val flowers = flowerRepository.saveAll(flowers1 + flowers2)
//
//        val biddingFlowers = flowers.map { flower ->
//            (1..10).map {
//                var date = LocalDateTime.now()
//                date = date.plusDays((it - 1).toLong())
//                BiddingFlower(flower = flower, biddingDate = date)
//            }
//        }
//        biddingFlowerRepository.saveAll(biddingFlowers.flatten())
//
//        logger.trace("createMockFlowers completed")
//    }
//
//    @Transactional
//    fun createMockCartItems() {
//        val cartItems = (1..5).map {
//            CartItem.createWith(
//                retailer = entityManager.getReference(User::class.java, 1.toLong()),
//                flower = entityManager.getReference(Flower::class.java, it.toLong()),
//                quantity = 10,
//                grade = EnumConverter.convertFlowerGradeToKor(FlowerGrade.UPPER)
//            )
//        }
//        cartItemRepository.saveAll(cartItems)
//        logger.trace("createMockCartItems completed")
//    }
//
//    // 3일정도 주기의 주문서
//    @Transactional
//    fun createMockOrderSheets() {
//
//        val orderSheets = (1..3).map {
//            OrderSheet(
//                orderNo = StringGenerator.generateOrderNo(it.toLong()),
//                retailer = entityManager.getReference(User::class.java, 1.toLong()),
//                wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
//            )
//        }
//        orderSheetRepository.saveAll(orderSheets)
//
//        val orderItems = (1..60).map {
//            val orderSheetId = ((it - 1) / 20) + 1
//            OrderItem(
//                orderSheet = entityManager.getReference(OrderSheet::class.java, orderSheetId.toLong()),
//                retailer = entityManager.getReference(User::class.java, 1.toLong()),
//                wholesaler = entityManager.getReference(User::class.java, 2.toLong()),
//                flower = entityManager.getReference(Flower::class.java, (1..10).random().toLong()),
//                quantity = (1..100).random(),
//                grade = EnumConverter.convertFlowerGradeToKor(FlowerGrade.UPPER),
//                price = (100..10000).random()
//            )
//        }
//        orderItemRepository.saveAll(orderItems)
//    }
//
//    fun generateRandomStr(outputStrLength: Long = 10): String {
//        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//        return Random().ints(outputStrLength, 0, source.length)
//            .asSequence()
//            .map(source::get)
//            .joinToString("")
//    }
//
//    fun generatePhoneNoStr(outputStrLength: Long = 8): String {
//        val source = "0123456789"
//        val postNo = Random().ints(outputStrLength, 0, source.length)
//            .asSequence()
//            .map(source::get)
//            .joinToString("")
//        return "010$postNo"
//    }
}
