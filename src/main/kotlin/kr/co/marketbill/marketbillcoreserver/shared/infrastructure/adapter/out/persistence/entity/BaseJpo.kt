package kr.co.marketbill.marketbillcoreserver.shared.infrastructure.adapter.out.persistence.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@SQLDelete(sql = "UPDATE {table} SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is Null")
abstract class BaseJpo : BaseTime()
