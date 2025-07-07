package kr.co.marketbill.marketbillcoreserver.shared.adapter.out.persistence.mapper

import com.querydsl.jpa.impl.JPAQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

fun <T> JPAQuery<T>.toPageJpoResponse(pageable: Pageable): Page<T> {
    val total = this.fetchCount()
    val content = this.offset(pageable.offset).limit(pageable.pageSize.toLong()).fetch()
    return PageImpl<T>(content, pageable, total)
}