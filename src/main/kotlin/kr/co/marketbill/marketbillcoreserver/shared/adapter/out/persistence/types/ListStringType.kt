package kr.co.marketbill.marketbillcoreserver.shared.adapter.out.persistence.types

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.UserType
import java.io.Serializable
import java.sql.*
import java.util.*

class ListStringType : UserType {

    override fun sqlTypes(): IntArray = intArrayOf(Types.ARRAY)
    override fun returnedClass(): Class<List<*>> = List::class.java as Class<List<*>>

    override fun equals(x: Any?, y: Any?): Boolean = Objects.equals(x, y)
    override fun hashCode(x: Any?): Int = x?.hashCode() ?: 0

    override fun nullSafeGet(
        rs: ResultSet,
        names: Array<String>,
        session: SharedSessionContractImplementor?,
        owner: Any?
    ): Any? {
        val array = rs.getArray(names[0]) ?: return emptyList<String>()
        val javaArray = array.array as Array<*>
        return javaArray.filterIsInstance<String>()
    }

    override fun nullSafeSet(
        st: PreparedStatement,
        value: Any?,
        index: Int,
        session: SharedSessionContractImplementor?
    ) {
        if (value == null) {
            st.setNull(index, Types.ARRAY)
        } else {
            val conn = st.connection
            val casted = (value as List<*>).filterIsInstance<String>().toTypedArray()
            val array = conn.createArrayOf("varchar", casted)
            st.setArray(index, array)
        }
    }

    override fun deepCopy(value: Any?): Any? = (value as? List<*>)?.toList()
    override fun isMutable(): Boolean = true
    override fun disassemble(value: Any?): Serializable? = value as? Serializable
    override fun assemble(cached: Serializable?, owner: Any?): Any? = cached
    override fun replace(original: Any?, target: Any?, owner: Any?): Any? = deepCopy(original)
}
