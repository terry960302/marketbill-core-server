package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderStatisticOutput
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.graphql.context.OrderContext
import kr.co.marketbill.marketbillcoreserver.graphql.dataloader.OrderItemLoader
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.service.CartService
import kr.co.marketbill.marketbillcoreserver.service.OrderService
import kr.co.marketbill.marketbillcoreserver.types.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestHeader
import java.time.LocalDate
import java.util.Optional
import java.util.concurrent.CompletableFuture

@DgsComponent
class OrderFetcher {
    @Autowired
    private lateinit var cartService: CartService

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    // query
    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetAllCartItems)
    fun getAllCartItems(
        @RequestHeader("Authorization") authorization: String,
        @InputArgument pagination: PaginationInput?
    ): Page<CartItem> {
        var pageable: Pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        if (pagination != null) {
            pageable = PageRequest.of(pagination.page!!, pagination.size!!)
        }
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.getAllCartItems(userId, pageable)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetOrderSheet)
    fun getOrderSheet(@InputArgument orderSheetId: Long): Optional<OrderSheet> {
        return orderService.getOrderSheet(orderSheetId)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetOrderSheets)
    fun getOrderSheets(
        @RequestHeader("Authorization") authorization: String?,
        @InputArgument filter: DateFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<OrderSheet> {
        var userId: Long? = null
        var role: AccountRole? = null
        var pageable: Pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        var date: LocalDate? = null

        if (authorization != null) {
            val token = jwtProvider.filterOnlyToken(authorization)
            userId = jwtProvider.parseUserId(token)
            role = jwtProvider.parseUserRole(token)
        }

        if (pagination != null) {
            val sort = if (pagination.sort == kr.co.marketbill.marketbillcoreserver.types.Sort.DESCEND) {
                Sort.by("createdAt").descending()
            } else {
                Sort.by("createdAt").ascending()
            }
            pageable = PageRequest.of(pagination.page!!, pagination.size!!, sort)
        }

        if (filter != null) {
            date = LocalDate.parse(filter.date)
        }

        return orderService.getOrderSheets(userId, role, date, pageable)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetOrderItems)
    fun getOrderItems(
        @InputArgument filter: DateFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<OrderItem> {
        var pageable: Pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        var date: LocalDate? = null

        if (pagination != null) {
            val sort = if (pagination.sort == kr.co.marketbill.marketbillcoreserver.types.Sort.DESCEND) {
                Sort.by("createdAt").descending()
            } else {
                Sort.by("createdAt").ascending()
            }
            pageable = PageRequest.of(pagination.page!!, pagination.size!!, sort)
        }

        if (filter != null) {
            date = LocalDate.parse(filter.date)
        }

        return orderService.getOrderItems(date, pageable)
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetDailyOrderStatistics)
    fun getDailyOrderStatistics(
        @RequestHeader authorization: String,
        @InputArgument pagination: PaginationInput?
    ): Page<OrderStatisticOutput> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId = jwtProvider.parseUserId(token)
        var pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)

        if (pagination != null) {
            pageable = PageRequest.of(pagination.page!!, pagination.size!!)
        }

        return orderService.getDailyOrderStatistics(userId, pageable)
    }


    // mutation
    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.AddToCart)
    fun addToCart(
        @RequestHeader("Authorization") authorization: String,
        @InputArgument input: AddToCartInput
    ): CartItem {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.addToCart(userId, input.flowerId.toLong(), input.quantity, FlowerGrade.valueOf(input.grade))
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.RemoveCartItem)
    fun removeCartItem(@InputArgument cartItemId: Long): Long {
        return cartService.removeCartItem(cartItemId)
    }

    @PreAuthorize("ROLE_RETAILER")
    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.UpsertWholesalerOnCartItems)
    fun upsertWholesalerOnCartItems(
        @RequestHeader("Authorization") authorization: String,
        @InputArgument wholesalerId: Long
    ): List<CartItem> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val retailerId = jwtProvider.parseUserId(token)
        return cartService.upsertWholesalerOnCartItems(retailerId, wholesalerId)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.OrderCartItems)
    fun orderCartItems(@InputArgument input: OrderCartItemsInput): OrderSheet {
        return orderService.orderCartItems(input.cartItemIds.map { it.toLong() }, input.wholesalerId.toLong())
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.RemoveOrderSheet)
    fun removeOrderSheet(@InputArgument orderSheetId: Long): Int {
        return orderService.removeOrderSheet(orderSheetId).toInt()
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.UpdateOrderItemsPrice)
    fun updateOrderItemsPrice(@InputArgument items: List<OrderItemPriceInput>): List<OrderItem> {
        return orderService.updateOrderItemsPrice(items)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.IssueOrderSheetReceipt)
    fun issueOrderSheetReceipt(@InputArgument orderSheetId: Long): kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt {
        return orderService.issueOrderSheetReceipt(orderSheetId)
    }


    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.BatchCartToOrder)
    fun batchCartToOrder(): Boolean {
        return try {
            cartService.batchCartToOrder()
            true
        } catch (e: Exception) {
            false
        }
    }


    // field
    @DgsData(parentType = DgsConstants.ORDERSHEET.TYPE_NAME, field = DgsConstants.ORDERSHEET.OrderItems)
    fun orderItems(
        dfe: DgsDataFetchingEnvironment,
        @InputArgument pagination: PaginationInput?
    ): CompletableFuture<List<OrderItem>> {
        val orderSheet = dfe.getSource<OrderSheet>()
        val dataLoader = dfe.getDataLoader<Long, List<OrderItem>>(OrderItemLoader::class.java)

        val orderContext = DgsContext.Companion.getCustomContext<OrderContext>(dfe)
        orderContext.pagination = pagination

        return dataLoader.load(orderSheet.id)
    }
}