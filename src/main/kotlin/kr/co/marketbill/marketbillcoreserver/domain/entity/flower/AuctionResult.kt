package kr.co.marketbill.marketbillcoreserver.domain.entity.flower

import kr.co.marketbill.marketbillcoreserver.domain.entity.common.BaseTime
import javax.persistence.*

//create table public.auction_results
//(
//id               bigserial,
//flower_name      varchar(50),
//flower_type_name varchar(50),
//flower_grade     varchar(10) default ''::character varying,
//box_count        integer,
//flower_count     integer,
//price            integer,
//total_price      integer,
//serial_code      varchar(50),
//created_at       timestamp,
//updated_at       timestamp,
//deleted_at       timestamp,
//wholesaler_id    bigint,
//auction_date     integer     default 0 not null
//);
//
//alter table public.auction_results
//owner to marketbill;
//
//create index ix_wholesalerid_auctiondate
//on public.auction_results (wholesaler_id, auction_date);
//


@Entity
@Table(name = "auction_results")
data class AuctionResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "flower_name")
    val flowerName: String? = null,

    @Column(name = "flower_type_name")
    val flowerTypeName: String? = null,

    @Column(name = "flower_grade")
    val flowerGrade: String? = null,

    @Column(name = "box_count")
    val boxCount: Int? = null,

    @Column(name = "flower_count")
    val flowerCount: Int? = null,

    @Column(name = "price")
    val price: Int? = null,

    @Column(name = "total_price")
    val totalPrice: Int? = null,

    @Column(name = "serial_code")
    val serialCode: String? = null,

    @Column(name = "wholesaler_id")
    val wholesalerId: Long? = null,

    @Column(name = "auction_date")
    val auctionDate: Int? = null
): BaseTime()