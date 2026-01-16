package com.achernov.cryptoarb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(length = 100, nullable = false, unique = true)
  private String email;

  @Column(length = 100)
  private String firstName;

  @Column(length = 100)
  private String lastName;

  @Column
  private LocalDate dateOfBirth;

  @Column(length = 20)
  private String phoneNumber;

  @Column(length = 200)
  private String addressStreet;

  @Column(length = 100)
  private String addressCity;

  @Column(length = 100)
  private String addressState;

  @Column(length = 100)
  private String addressCountry;

  @Column(length = 20)
  private String addressZipCode;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(length = 50, nullable = false)
  private Role role = Role.USER;

  @Builder.Default
  @Column(nullable = false)
  private boolean enabled = true;

  @Column
  private LocalDateTime lastLoginAt;

  @Column(nullable = false, updatable = false, insertable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false, insertable = false)
  private LocalDateTime updatedAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public enum Role {
    USER,
    ADMIN,
  }
}
