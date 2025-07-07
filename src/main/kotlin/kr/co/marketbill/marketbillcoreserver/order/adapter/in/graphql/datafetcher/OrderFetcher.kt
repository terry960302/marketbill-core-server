package kr.co.marketbill.marketbillcoreserver.order.adapter.`in`.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.order.application.service.OrderService
import kr.co.marketbill.marketbillcoreserver.order.application.command.*
import kr.co.marketbill.marketbillcoreserver.order.adapter.`in`.graphql.mapper.OrderOutputMapper
import kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.context.getUserIdFromContext
import kr.co.marketbill.marketbillcoreserver.shared.domain.model.CustomUserDetails
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import kr.co.marketbill.marketbillcoreserver.types.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import java.time.LocalDate
import kr.co.marketbill.marketbillcoreserver.types.DailyOrderItemFilterInput as GqlDailyOrderItemFilterInput
import kr.co.marketbill.marketbillcoreserver.types.OrderItemPriceInput as GqlOrderItemPriceInput
import kr.co.marketbill.marketbillcoreserver.types.UpsertCustomOrderItemsInput as GqlUpsertCustomOrderItemsInput

@DgsComponent
class OrderFetcher(
    private val orderService: OrderService,
    private val orderOutputMapper: OrderOutputMapper
) {

    @DgsQuery
    fun orderSheets(
        @InputArgument filter: DateFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): List<OrderSheet> {
        val command = OrderSheetSearchCommand(filter, pagination)
        return orderService.getOrderSheets(command)
            .map { orderOutputMapper.toOrderSheetOutput(it) }
    }

    @DgsQuery
    fun orderSheet(@InputArgument orderSheetId: Int): OrderSheet {
        val command = OrderSheetDetailCommand(orderSheetId)
        return orderOutputMapper.toOrderSheetOutput(orderService.getOrderSheet(command))
    }

    @DgsQuery
    fun orderItems(
        @InputArgument filter: DateFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): List<OrderItem> {
        val command = OrderItemSearchCommand.from(filter, pagination)
        return orderService.getOrderItems(command)
            .map { orderOutputMapper.toOrderItemOutput(it) }
    }

    @DgsQuery
    fun dailyOrderSheetAggregates(
        @InputArgument pagination: PaginationInput?,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): PageResult<OrderSheetsAggregate> {
        val command =
            DailyOrderSheetAggregateByDateRangeCommand.from(userId = userDetails.getUserId().value, pagination)
        return orderService.getDailyOrderSheetAggregatesUntilPast3Months(command)
            .map { orderOutputMapper.toOrderSheetsAggregateOutput(it) }
    }

    @DgsQuery
    fun dailyOrderSheetAggregate(@InputArgument date: LocalDate?): OrderSheetsAggregate? {
        val command = DailyOrderSheetAggregateByDateCommand.from(date)
        return orderService.getDailyOrderSheetAggregate(command)
            ?.let { orderOutputMapper.toOrderSheetsAggregateOutput(it) }
    }

    @DgsQuery
    fun dailyOrderItems(
        @InputArgument filter: GqlDailyOrderItemFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): List<DailyOrderItem> {
        val command = DailyOrderItemSearchCommand.from(filter, pagination)
        return orderService.getDailyOrderItems(command)
            .map { orderOutputMapper.toDailyOrderItemOutput(it) }
    }

    @DgsMutation
    fun orderCartItems(): OrderSheet? {
        val userId = getUserIdFromContext().value.toInt()
        val command = OrderCartItemsCommand.from(userId)
        return orderService.orderCartItems(command)
            ?.let { orderOutputMapper.toOrderSheetOutput(it) }
    }

    @DgsMutation
    fun removeOrderSheet(@InputArgument orderSheetId: Int): CommonResponse {
        val command = RemoveOrderSheetCommand.from(orderSheetId)
        val success = orderService.removeOrderSheet(command)
        return CommonResponse(success = success)
    }

    @DgsMutation
    fun updateOrderItemsPrice(@InputArgument items: List<GqlOrderItemPriceInput>): List<OrderItem> {
        val command = UpdateOrderItemsPriceCommand.from(items)
        return orderService.updateOrderItemsPrice(command)
            .map { orderOutputMapper.toOrderItemOutput(it) }
    }

    @DgsMutation
    fun upsertCustomOrderItems(@InputArgument input: GqlUpsertCustomOrderItemsInput): List<CustomOrderItem> {
        val command = UpsertCustomOrderItemsCommand.from(input)
        return orderService.upsertCustomOrderItems(command)
            .map { orderOutputMapper.toCustomOrderItemOutput(it) }
    }

    @DgsMutation
    fun removeCustomOrderItem(@InputArgument customOrderItemIds: List<Int>): CommonResponse {
        val command = RemoveCustomOrderItemCommand.from(customOrderItemIds)
        val success = orderService.removeCustomOrderItem(command)
        return CommonResponse(success = success)
    }

    @DgsMutation
    fun updateDailyOrderItemsPrice(@InputArgument items: List<GqlOrderItemPriceInput>): List<DailyOrderItem> {
        val command = UpdateDailyOrderItemsPriceCommand.from(items)
        return orderService.updateDailyOrderItemsPrice(command)
            .map { orderOutputMapper.toDailyOrderItemOutput(it) }
    }

    @DgsMutation
    fun issueOrderSheetReceipt(@InputArgument orderSheetId: Int): OrderSheetReceipt {
        val command = IssueOrderSheetReceiptCommand.from(orderSheetId)
        return orderOutputMapper.toOrderSheetReceiptOutput(orderService.issueOrderSheetReceipt(command))
    }

    @DgsMutation
    fun orderBatchCartItems(): CommonResponse {
        // TODO: 배치 주문 처리 로직 구현
        return CommonResponse(success = true)
    }
}