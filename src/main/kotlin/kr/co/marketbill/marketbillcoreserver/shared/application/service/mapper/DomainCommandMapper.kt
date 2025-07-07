package kr.co.marketbill.marketbillcoreserver.shared.application.service.mapper

abstract class DomainCommandMapper<Command, Domain> {
    abstract fun toDomain(command : Command) : Domain
}