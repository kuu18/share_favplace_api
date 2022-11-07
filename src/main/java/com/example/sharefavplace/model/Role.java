package com.example.sharefavplace.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role extends AbstractEntity implements GrantedAuthority {
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
  @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1)
  @Id
  @Column(name = "id")
  private Integer id;
  @Column(name = "rolename")
	private String rolename;

  @Override
  public String getAuthority() {
    return this.rolename;
  }
}
