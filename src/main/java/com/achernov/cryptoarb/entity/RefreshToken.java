package com.achernov.cryptoarb.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String token;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Instant expiryDate;

  @Column(length = 100)
  private String deviceId;

  @Column(length = 45)
  private String ipAddress;

  @Column
  private String userAgent;

  @Column(updatable = false)
  private Instant createdAt;
}
