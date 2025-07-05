package kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.mapper

abstract class InputCommandMapper<Input, Command>{
    abstract fun toCommand(input : Input) : Command
}