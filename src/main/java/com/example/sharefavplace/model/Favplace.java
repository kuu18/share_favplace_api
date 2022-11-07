package com.example.sharefavplace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favplaces")
public class Favplace extends AbstractEntity {
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favplace_id_seq")
  @SequenceGenerator(name = "favplace_id_seq", sequenceName = "favplace_id_seq", allocationSize = 1)
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
  @ManyToMany
  @JoinTable(
    name="favplaces_categories",
    joinColumns = @JoinColumn(name="favplace_id", referencedColumnName="id"),
    inverseJoinColumns = @JoinColumn(name="category_id", referencedColumnName="id")
  )
  private List<Category> categories = new ArrayList<>();
  @Column(name = "reference_url")
  private String referenceUrl;
  @Column(name = "image_url")
  private String imageUrl;
  @Column(name = "remarks")
  private String remarks;
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Transient
  private String categoryName;

  public String getCategoryName() {
    if(getCategories().isEmpty()) return categoryName;
    return getCategories().stream().map(category -> category.getCategoryname())
      .collect(Collectors.joining("、"));
  }
}
