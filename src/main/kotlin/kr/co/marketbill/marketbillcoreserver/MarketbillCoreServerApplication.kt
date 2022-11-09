package kr.co.marketbill.marketbillcoreserver

import kr.co.marketbill.marketbillcoreserver.config.SecurityConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MarketbillCoreServerApplication

fun main(args: Array<String>) {
	runApplication<MarketbillCoreServerApplication>(*args)
}
