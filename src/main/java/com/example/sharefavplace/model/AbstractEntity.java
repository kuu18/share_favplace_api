package com.example.sharefavplace.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * DB共通Entity
 */

@Getter
@Setter
@MappedSuperclass
public class AbstractEntity {
  /** 登録日時 */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at")
  private Date createdAt;

  /** 更新日時 */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at")
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
