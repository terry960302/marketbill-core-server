package kr.co.marketbill.marketbillcoreserver.user.adapter.out.persistence.entity

import kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity.BaseJpo
import javax.persistence.*

@Entity
@Table(name = "users")
class UserJpo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "belongs_to", nullable = true)
    val belongsTo: String? = null,

    @Column(name = "name")
    val name: String,

    @OneToOne(
        mappedBy = "userJpo",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    )
    val userCredentialJpo: UserCredentialJpo? = null,

    @OneToOne(
        mappedBy = "userJpo",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val authTokenJpo: AuthTokenJpo? = null,

    @OneToOne(
        mappedBy = "userJpo",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val businessInfoJpo: BusinessInfoJpo? = null,

    @OneToMany(
        mappedBy = "retailer",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val appliedConnections: List<BizConnectionJpo> = listOf(),

    @OneToMany(
        mappedBy = "wholesaler",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val receivedConnections: List<BizConnectionJpo> = listOf(),

    @OneToMany(
        mappedBy = "employer",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val wholesalerConnectionsByEmployerJpo: List<WholesalerConnectionJpo> = listOf(),

    @OneToMany(
        mappedBy = "employee",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val wholesalerConnectionsByEmployeeJpo: List<WholesalerConnectionJpo> = listOf(),

//    @OneToMany(
//        mappedBy = "retailer",
//        cascade = [CascadeType.ALL],
//        orphanRemoval = true,
//        fetch = FetchType.LAZY
//    )
//    val retailerCartItems: List<CartItem> = listOf(),
//
//    @OneToMany(
//        mappedBy = "wholesaler",
//        cascade = [CascadeType.ALL],
//        orphanRemoval = true,
//        fetch = FetchType.LAZY
//    )
//    val wholesalerCartItems: List<CartItem> = listOf(),
//
//    @OneToMany(mappedBy = "retailer", fetch = FetchType.LAZY)
//    val orderSheetsByRetailer: List<OrderSheet> = listOf(),
//
//    @OneToMany(mappedBy = "wholesaler", fetch = FetchType.LAZY)
//    val orderSheetsByWholesaler: List<OrderSheet> = listOf(),
//
//    @OneToMany(
//        mappedBy = "retailer",
//        fetch = FetchType.LAZY,
//    )
//    val orderItemsByRetailer: List<OrderItem> = listOf(),
//
//    @OneToMany(
//        mappedBy = "wholesaler",
//        fetch = FetchType.LAZY,
//    )
//    val orderItemsByWholesaler: List<OrderItem> = listOf(),
//
//    @OneToOne(mappedBy = "retailer")
//    val retailerShoppingSession: ShoppingSession? = null,
//
//    @OneToMany(mappedBy = "wholesaler")
//    val wholesalerShoppingSessions: List<ShoppingSession> = listOf(),
//
//    @Transient
//    var connectedEmployer: UserJpo? = null,
//
//    @Transient
//    var connectedEmployees: List<UserJpo> = listOf(),
) : BaseJpo() {

    companion object {
        fun create(id: Long? = null, name: String, belongsTo: String? = null): UserJpo {
            require(name.isNotBlank()) { "name must not be blank" }
            return UserJpo(id = id, name = name, belongsTo = belongsTo)
        }
    }

//    fun mapConnectedEmployer() {
//        if (wholesalerConnectionsByEmployeeJpo.isNotEmpty()) {
//            val conns = wholesalerConnectionsByEmployeeJpo.mapNotNull { it.employer }
//            if (conns.isNotEmpty()) {
//                connectedEmployer = wholesalerConnectionsByEmployeeJpo.mapNotNull { it.employer }[0]
//            }
//        }
//    }
//
//    fun mapConnectedEmployees() {
//        if (wholesalerConnectionsByEmployerJpo.isNotEmpty()) {
//            connectedEmployees = wholesalerConnectionsByEmployerJpo.mapNotNull { it.employee }
//        }
//    }
}
