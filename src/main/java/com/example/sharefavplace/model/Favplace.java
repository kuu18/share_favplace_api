package com.example.sharefavplace.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favplaces", uniqueConstraints = {
  @UniqueConstraint(name = "favplaces_user_id_fk", columnNames = {"user_id"}),
  @UniqueConstraint(name = "favplaces_category_id_fk", columnNames = {"category_id"}),
  @UniqueConstraint(name = "favplaces_schedule_id_fk", columnNames = {"schedule_id"})
})
public class Favplace extends AbstractEntity {
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favplaces_id_seq")
  @SequenceGenerator(name = "favplaces_id_seq", sequenceName = "favplaces_id_seq", allocationSize = 1)
  @Id
  @Column(name = "id")
  private Integer id;
  @Column(name = "favplacename")
	private String favplacename;
  @Column(name = "prefecture")
  private String prefecture;
  @Column(name = "municipality")
  private String municipality;
  @Column(name = "address")
  private String address;
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  private Category category;
  @Column(name = "reference_url")
  private String referenceUrl;
  @Column(name = "image_url")
  private String imageUrl;
  @Column(name = "remarks")
  private String remarks;
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;
  @OneToOne
  @JsonIgnore
  private Schedule schedule;

  public Integer getScheduleId() {
    return schedule != null ? schedule.getId() : null;
  }

  public String getCategoryName() {
    return category != null ? category.getCategoryname() : null;
  }

}
