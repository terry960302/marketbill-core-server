package kr.co.marketbill.marketbillcoreserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.TimeZone
import javax.annotation.PostConstruct

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
class MarketbillCoreServerApplication
fun main(args: Array<String>) {
	runApplication<MarketbillCoreServerApplication>(*args)
}
