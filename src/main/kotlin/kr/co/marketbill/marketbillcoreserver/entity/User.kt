package kr.co.marketbill.marketbillcoreserver.entity

import kr.co.marketbill.marketbillcoreserver.entity.common.BaseTime
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
    val name: String = "",

    @Column(name = "business_no", nullable = true)
    val businessNo: String? = null,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true) // cascade 부모 삭제하면 자식 전체 삭제, orphanRemoval 자식인 경우 삭제하면 부모도 삭제
    val userCredential: UserCredential? = null,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val authToken: AuthToken? = null,

    @OneToMany(mappedBy = "retailer")
    val retailerToWholesaler : List<BizConnection> = arrayListOf(),

    @OneToMany(mappedBy = "wholesaler")
    val wholesalerToRetailer : List<BizConnection> = arrayListOf(),

    @OneToMany(mappedBy = "retailer")
    val cartItems : List<CartItem> = arrayListOf(),

    @OneToMany(mappedBy = "retailer")
    val orderSheetsByRetailer : List<OrderSheet> = arrayListOf(),

    @OneToMany(mappedBy = "wholesaler")
    val orderSheetsByWholesaler : List<OrderSheet> = arrayListOf(),

    @OneToMany(mappedBy = "retailer")
    val orderItemsByRetailer : List<OrderItem> = arrayListOf(),

    @OneToMany(mappedBy = "wholesaler")
    val orderItemsByWholesaler : List<OrderItem> = arrayListOf(),


) : BaseTime() {
}