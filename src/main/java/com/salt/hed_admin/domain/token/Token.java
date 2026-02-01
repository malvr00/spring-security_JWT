package com.salt.hed_admin.domain.token;

import com.salt.hed_admin.domain.permission.enums.PlatformType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Entity
@Builder
@Table(name = "token", indexes = {
        @Index(name = "idx_user_platform_revoked", columnList = "userId,platform,revoked"),
        @Index(name = "ux_refresh_jti", columnList = "refreshJti", unique = true)})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private PlatformType platform;

    private String refreshJti;
    private String refreshHash;

    private Timestamp refreshExpiresAt;

    private boolean revoked;
    private Timestamp revokedAt;

    public void updateRevoked(boolean b) {
        this.revoked = b;
    }

}
