package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.annotation.PostConstruct
import javax.persistence.*

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    /**
     * <도매상>
     *
     *     : 시장명이 들어감(양재, 경부선 등)
     * <소매상>
     *
     *     : 빈값
     */
    @Column(name = "belongs_to", nullable = true)
    val belongsTo: String? = null,

    @Column(name = "name")
    val name: String? = null,

    @OneToOne(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    ) // cascade 부모 삭제하면 자식 전체 삭제, orphanRemoval 자식인 경우 삭제하면 부모도 삭제
    val userCredential: UserCredential? = null,

    @OneToOne(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val authToken: AuthToken? = null,

    @OneToOne(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val businessInfo: BusinessInfo? = null,

    @OneToMany(
        mappedBy = "retailer",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val appliedConnections: List<BizConnection> = listOf(),

    @OneToMany(
        mappedBy = "wholesaler",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val receivedConnections: List<BizConnection> = listOf(),

    // 내가 사장일 경우의 직원과의 관계
    @OneToMany(
        mappedBy = "employer",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val wholesalerConnectionsByEmployer: List<WholesalerConnection> = listOf(),

    // 내가 직원일 경우의 사장과의 관계
    @OneToMany(
        mappedBy = "employee",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val wholesalerConnectionsByEmployee: List<WholesalerConnection> = listOf(),

    @OneToMany(
        mappedBy = "retailer",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val retailerCartItems: List<CartItem> = listOf(),

    @OneToMany(
        mappedBy = "wholesaler",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val wholesalerCartItems: List<CartItem> = listOf(),

    @OneToMany(
        mappedBy = "retailer",
        fetch = FetchType.LAZY
    )
    val orderSheetsByRetailer: List<OrderSheet> = listOf(),

    @OneToMany(
        mappedBy = "wholesaler",
        fetch = FetchType.LAZY
    )
    val orderSheetsByWholesaler: List<OrderSheet> = listOf(),

    @OneToMany(
        mappedBy = "retailer",
        fetch = FetchType.LAZY,
    )
    val orderItemsByRetailer: List<OrderItem> = listOf(),

    @OneToMany(
        mappedBy = "wholesaler",
        fetch = FetchType.LAZY,
    )
    val orderItemsByWholesaler: List<OrderItem> = listOf(),

    @Transient
    var applyStatus: ApplyStatus? = null,

    @Transient
    var bizConnectionId: Long? = null,

    @Transient
    var connectedEmployer: User? = null,

    @Transient
    var connectedEmployees: List<User> = listOf(),
) : BaseTime() {

    fun mapConnectedEmployer() {
        if (wholesalerConnectionsByEmployee.isNotEmpty()) {
            val conns = wholesalerConnectionsByEmployee.mapNotNull { it.employer }
            if (conns.isNotEmpty()) {
                connectedEmployer = wholesalerConnectionsByEmployee.mapNotNull { it.employer }[0]
            }
        }
    }

    fun mapConnectedEmployees() {
        if (wholesalerConnectionsByEmployer.isNotEmpty()) {
            connectedEmployees = wholesalerConnectionsByEmployer.mapNotNull { it.employee }
        }
    }

}