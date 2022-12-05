package com.example.sharefavplace.model;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import javax.persistence.Transient;

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
@Table(name = "schedules")
public class Schedule extends AbstractEntity {
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedules_id_seq")
  @SequenceGenerator(name = "schedules_id_seq", sequenceName = "schedules_id_seq", allocationSize = 1)
  @Id
  @Column(name = "id")
  private Integer id;
  @Column(name = "start_day")
  private Date start;
  @Column(name = "end_day")
  private Date end;
  @Column(name = "timed")
  private Boolean timed;
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  private User user;
  @OneToOne
  private Favplace favplace;

  @Transient
  @JsonIgnore
  private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
  @Transient
  @JsonIgnore
  private SimpleDateFormat dfwithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  public String getName() {
    return favplace.getFavplacename();
  }

  public Integer getUserId() {
    return user.getId();
  }

  public String getColor() {
    return favplace.getCategory() != null ? favplace.getCategory().getColor() : null;
  }

  public String getStart() {
    return timed ? dfwithTime.format(start) : df.format(start);
  }

  public String getEnd() {
    return timed ? dfwithTime.format(end) : df.format(end);
  }

}
