package kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.FlowerJpo
import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerSearchCriteria
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerTypeId
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class FlowerQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    private val flower = QFlowerJpo.flowerJpo
    private val flowerType = QFlowerTypeJpo.flowerTypeJpo

    fun findFlowersWithDynamicQuery(
        criteria: FlowerSearchCriteria,
        pageInfo: PageInfo
    ): PageResult<FlowerJpo> {

        val query = queryFactory
            .selectFrom(flower)
            .leftJoin(flower.flowerTypeJpo, flowerType).fetchJoin()
            .where(buildConditions(criteria))
            .orderBy(flower.createdAt.desc())

        // Count 쿼리 최적화
        val countQuery = queryFactory
            .select(flower.count())
            .from(flower)
            .leftJoin(flower.flowerTypeJpo, flowerType)
            .where(buildConditions(criteria))

        val totalCount = countQuery.fetchOne() ?: 0L

        if (totalCount == 0L) {
            return PageResult.empty(pageInfo)
        }

        val results = query
            .offset((pageInfo.page * pageInfo.size).toLong())
            .limit(pageInfo.size.toLong())
            .fetch()

        return PageResult(
            content = results,
            pageInfo = pageInfo,
            totalElements = totalCount
        )
    }

    private fun buildConditions(criteria: FlowerSearchCriteria): BooleanExpression? {
        return listOfNotNull(
            dateRangeCondition(criteria.fromDate, criteria.toDate),
            keywordCondition(criteria.keyword),
            flowerTypeCondition(criteria.flowerTypeId)
        ).reduceOrNull { acc, condition -> acc.and(condition) }
    }

    private fun dateRangeCondition(fromDate: LocalDate?, toDate: LocalDate?): BooleanExpression? {
        return when {
            fromDate != null && toDate != null ->
                flower.createdAt.between(fromDate.atStartOfDay(), toDate.atTime(23, 59, 59))
            fromDate != null ->
                flower.createdAt.goe(fromDate.atStartOfDay())
            toDate != null ->
                flower.createdAt.loe(toDate.atTime(23, 59, 59))
            else -> null
        }
    }

    private fun keywordCondition(keyword: String?): BooleanExpression? {
        return if (!keyword.isNullOrBlank()) {
            flower.name.containsIgnoreCase(keyword)
                .or(flowerType.name.containsIgnoreCase(keyword))
        } else null
    }

    private fun flowerTypeCondition(flowerTypeId: FlowerTypeId?): BooleanExpression? {
        return flowerTypeId?.let { flower.flowerTypeJpo.id.eq(it.value) }
    }
}
