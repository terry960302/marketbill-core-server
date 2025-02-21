package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

import com.vladmihalcea.hibernate.type.array.ListArrayType
import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import org.hibernate.annotations.OrderBy
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.Where
import javax.persistence.*
// 품종
@Entity
@Table(name = "flowers")
@SQLDelete(sql = "UPDATE flowers SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
@TypeDef(name = "list-string", typeClass = ListArrayType::class)
class Flower(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "name")
        val name: String = "",

        @Type(type = "list-string")
        @Column(name = "images", columnDefinition = "varchar(255)[]")
        @OrderBy(clause = "image desc")
        val images: List<String> = emptyList(),

        @ManyToOne
        @JoinColumn(name = "flower_type_id")
        val flowerType: FlowerType? = null,

        @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY)
        val biddingFlowers: List<BiddingFlower>? = null,

        @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY)
        val cartItems: List<CartItem>? = null,

        @OneToMany(mappedBy = "flower", fetch = FetchType.LAZY)
        val orderItems: List<OrderItem>? = null,

        @ManyToOne
        @JoinColumn(name = "flower_color_id")
        val flowerColor: FlowerColor? = null,
) : BaseTime()