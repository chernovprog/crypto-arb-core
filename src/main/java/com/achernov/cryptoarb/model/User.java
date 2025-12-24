package com.achernov.cryptoarb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class User {

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

  @Enumerated(EnumType.STRING)
  @Column(length = 50, nullable = false)
  private Role role = Role.USER;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column
  private LocalDateTime lastLoginAt;

  @Column(nullable = false, updatable = false, insertable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false, insertable = false)
  private LocalDateTime updatedAt;

  public enum Role {
    USER,
    ADMIN,
  }
}
