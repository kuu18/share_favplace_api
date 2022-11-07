package com.example.sharefavplace.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@Table(name = "users")
public class User extends AbstractEntity implements UserDetails{
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
  @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
  @Id
  @Column(name = "id")
  private Integer id;
  @Column(name = "username")
	private String username;
  @Column(name = "email")
  private String email;
  @Column(name = "password")
  @JsonIgnore
  private String password;
  @Column(name = "activated")
  @JsonIgnore
  private Boolean activated;
  @Column(name = "avatar_url")
  private String avatarUrl;
  @Column(name = "roles")
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name="users_roles",
    joinColumns = @JoinColumn(name="user_id", referencedColumnName="id"),
    inverseJoinColumns = @JoinColumn(name="role_id", referencedColumnName="id")
  )
  private List<Role> roles = new ArrayList<>();
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Favplace> favplaces = new ArrayList<>();

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }

  public List<String> getKeys() {
    return new ArrayList<>(
      Arrays.asList(
        "id", 
        "username",
        "email",
        "password",
        "avatarUrl",
        "roles"
      )
    );
  }
}
