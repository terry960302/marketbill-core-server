package kr.co.marketbill.marketbillcoreserver.shared.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class QueryDslConfig {
    @Bean
    fun jpaQueryFactory(em : EntityManager): JPAQueryFactory = JPAQueryFactory(em)
}