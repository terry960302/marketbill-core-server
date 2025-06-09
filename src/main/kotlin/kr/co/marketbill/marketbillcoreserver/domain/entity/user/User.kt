package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import javax.persistence.*
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.SoftDeleteEntity
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus

@Entity
@Table(name = "users")
class User protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "belongs_to", nullable = true)
    val belongsTo: String? = null,

    @Column(name = "name")
    val name: String,

    @OneToOne(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    )
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

    @OneToMany(
        mappedBy = "employer",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val wholesalerConnectionsByEmployer: List<WholesalerConnection> = listOf(),

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

    @OneToMany(mappedBy = "retailer", fetch = FetchType.LAZY)
    val orderSheetsByRetailer: List<OrderSheet> = listOf(),

    @OneToMany(mappedBy = "wholesaler", fetch = FetchType.LAZY)
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

    @OneToOne(mappedBy = "retailer")
    val retailerShoppingSession: ShoppingSession? = null,

    @OneToMany(mappedBy = "wholesaler")
    val wholesalerShoppingSessions: List<ShoppingSession> = listOf(),

    @Transient
    var applyStatus: ApplyStatus? = null,

    @Transient
    var bizConnectionId: Long? = null,

    @Transient
    var connectedEmployer: User? = null,

    @Transient
    var connectedEmployees: List<User> = listOf(),
) : SoftDeleteEntity() {

    companion object {
        fun builder(name: String, belongsTo: String? = null, id: Long? = null): User {
            require(name.isNotBlank()) { "name must not be blank" }
            return User(id = id, name = name, belongsTo = belongsTo)
        }
    }

    fun updateApplyInfo(status: ApplyStatus?, connectionId: Long?) {
        this.applyStatus = status
        this.bizConnectionId = connectionId
    }

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
