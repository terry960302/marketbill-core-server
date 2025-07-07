package kr.co.marketbill.marketbillcoreserver.flower.application.result

import kr.co.marketbill.marketbillcoreserver.flower.domain.model.FlowerColor

data class FlowerColorResult(val id: Long, val name: String) {

    companion object {
        fun from(domain: FlowerColor): FlowerColorResult {
            return FlowerColorResult(domain.id!!.value, domain.name)
        }
    }
}