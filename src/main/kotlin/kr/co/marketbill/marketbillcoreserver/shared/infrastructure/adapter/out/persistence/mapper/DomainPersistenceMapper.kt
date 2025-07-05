package kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.mapper

abstract class DomainPersistenceMapper<Domain, Jpo> {
    abstract fun toDomain(jpo : Jpo) : Domain
    abstract fun toJpo(domain : Domain) : Jpo
}