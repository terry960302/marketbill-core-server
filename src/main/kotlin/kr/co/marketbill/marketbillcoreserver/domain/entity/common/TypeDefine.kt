package kr.co.marketbill.marketbillcoreserver.domain.entity.common

import com.vladmihalcea.hibernate.type.array.ListArrayType
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs

@TypeDefs(
        TypeDef(
                name = "list-string",
                typeClass = ListArrayType::class
        )
)
class TypeDefine
