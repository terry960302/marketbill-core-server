package kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.mapper

abstract class BaseMapper<Domain, Jpo> {
    abstract fun toDomain(jpo : Jpo) : Domain
    abstract fun toJpo(domain : Domain) : Jpo
}