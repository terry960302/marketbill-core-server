package kr.co.marketbill.marketbillcoreserver.order.application.service

import kr.co.marketbill.marketbillcoreserver.order.application.command.*
import kr.co.marketbill.marketbillcoreserver.order.application.result.*
import kr.co.marketbill.marketbillcoreserver.order.application.usecase.*
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderSheetSearchUseCase: OrderSheetSearchUseCase,
    private val orderSheetDetailUseCase: OrderSheetDetailUseCase,
    private val orderItemSearchUseCase: OrderItemSearchUseCase,
    private val dailyOrderSheetAggregateUseCase: DailyOrderSheetAggregateUseCase,
    private val dailyOrderItemSearchUseCase: DailyOrderItemSearchUseCase,
    private val orderCartItemsUseCase: OrderCartItemsUseCase,
    private val removeOrderSheetUseCase: RemoveOrderSheetUseCase,
    private val updateOrderItemsPriceUseCase: UpdateOrderItemsPriceUseCase,
    private val upsertCustomOrderItemsUseCase: UpsertCustomOrderItemsUseCase,
    private val removeCustomOrderItemUseCase: RemoveCustomOrderItemUseCase,
    private val updateDailyOrderItemsPriceUseCase: UpdateDailyOrderItemsPriceUseCase,
    private val issueOrderSheetReceiptUseCase: IssueOrderSheetReceiptUseCase
) {
    // Query 메서드들
    fun getOrderSheets(command: OrderSheetSearchCommand): List<OrderSheetResult> {
        return orderSheetSearchUseCase.execute(command)
    }

    fun getOrderSheet(command: OrderSheetDetailCommand): OrderSheetResult {
        return orderSheetDetailUseCase.execute(command)
    }

    fun getOrderItems(command: OrderItemSearchCommand): List<OrderItemResult> {
        return orderItemSearchUseCase.execute(command)
    }

    fun getDailyOrderSheetAggregatesUntilPast3Months(
        command: DailyOrderSheetAggregateByDateRangeCommand
    ): PageResult<OrderSheetsAggregateResult> {
        return dailyOrderSheetAggregateUseCase.executeUntilPast3Months(command)
    }

    fun getDailyOrderSheetAggregate(
        command: DailyOrderSheetAggregateByDateCommand
    ): OrderSheetsAggregateResult? {
        return dailyOrderSheetAggregateUseCase.executeByDate(command)
    }

    fun getDailyOrderItems(command: DailyOrderItemSearchCommand): List<DailyOrderItemResult> {
        return dailyOrderItemSearchUseCase.execute(command)
    }

    // Mutation 메서드들
    @Transactional
    fun orderCartItems(command: OrderCartItemsCommand): OrderSheetResult? {
        return orderCartItemsUseCase.execute(command)
    }

    @Transactional
    fun removeOrderSheet(command: RemoveOrderSheetCommand): Boolean {
        return removeOrderSheetUseCase.execute(command)
    }

    @Transactional
    fun updateOrderItemsPrice(command: UpdateOrderItemsPriceCommand): List<OrderItemResult> {
        return updateOrderItemsPriceUseCase.execute(command)
    }

    @Transactional
    fun upsertCustomOrderItems(
        command: UpsertCustomOrderItemsCommand
    ): List<CustomOrderItemResult> {
        return upsertCustomOrderItemsUseCase.execute(command)
    }

    @Transactional
    fun removeCustomOrderItem(command: RemoveCustomOrderItemCommand): Boolean {
        return removeCustomOrderItemUseCase.execute(command)
    }

    @Transactional
    fun updateDailyOrderItemsPrice(
        command: UpdateDailyOrderItemsPriceCommand
    ): List<DailyOrderItemResult> {
        return updateDailyOrderItemsPriceUseCase.execute(command)
    }

    @Transactional
    fun issueOrderSheetReceipt(command: IssueOrderSheetReceiptCommand): OrderSheetReceiptResult {
        return issueOrderSheetReceiptUseCase.execute(command)
    }
}
