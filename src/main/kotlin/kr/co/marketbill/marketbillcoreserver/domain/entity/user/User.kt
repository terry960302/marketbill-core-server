package kr.co.marketbill.marketbillcoreserver.domain.entity.user

import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.ApplyStatus
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name")
    val name: String? = null,

    @Column(name = "business_no", nullable = true)
    val businessNo: String? = null,

    @OneToOne(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    ) // cascade 부모 삭제하면 자식 전체 삭제, orphanRemoval 자식인 경우 삭제하면 부모도 삭제
    val userCredential: UserCredential? = null,

    @OneToOne(
        mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY,
    )
    val authToken: AuthToken? = null,

    @OneToMany(mappedBy = "retailer", fetch = FetchType.LAZY)
    val appliedConnections: List<BizConnection>? = null,

    @OneToMany(mappedBy = "wholesaler", fetch = FetchType.LAZY)
    val receivedConnections: List<BizConnection>? = null,

    @OneToMany(mappedBy = "retailer")
    val retailerCartItems: List<CartItem>? = null,

    @OneToMany(mappedBy = "wholesaler")
    val wholesalerCartItems: List<CartItem>? = null,

    @OneToMany(mappedBy = "retailer")
    val orderSheetsByRetailer: List<OrderSheet>? = null,

    @OneToMany(mappedBy = "wholesaler")
    val orderSheetsByWholesaler: List<OrderSheet>? = null,

    @OneToMany(mappedBy = "retailer")
    val orderItemsByRetailer: List<OrderItem>? = null,

    @OneToMany(mappedBy = "wholesaler")
    val orderItemsByWholesaler: List<OrderItem>? = null,

    @Transient
    var applyStatus: ApplyStatus? = null,

    @Transient
    var bizConnectionId : Long? = null,


    ) : BaseTime() {
}