package kr.co.marketbill.marketbillcoreserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class MarketbillCoreServerApplication

fun main(args: Array<String>) {
	runApplication<MarketbillCoreServerApplication>(*args)
}
