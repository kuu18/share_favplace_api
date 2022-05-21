package com.example.sharefavplace.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
/**
 * DB共通Entity
 */

@Data
@MappedSuperclass
public abstract class AbstractEntity {
  /** 登録日時 */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", nullable = false, updatable = false)
  private Date createdAt;

  /** 更新日時 */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at", nullable = false)
  private Date updatedAt;

  /** 登録日時の設定 */
  @PrePersist
  public void onPrePersist() {
    setCreatedAt(new Date());
    setUpdatedAt(new Date());
  }

  /** 更新日時の設定 */
  @PreUpdate
  public void onPreUpdate() {
    setUpdatedAt(new Date());
  }
}
