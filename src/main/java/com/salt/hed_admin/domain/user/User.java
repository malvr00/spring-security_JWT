package com.salt.hed_admin.domain.user;

import com.salt.hed_admin.domain.BaseTimeEntity;
import com.salt.hed_admin.domain.user.enums.UserStateEnum;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Entity
@Builder
@Table(name = "admin_user")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_user_id")
    private Long id;

    @Column(nullable = false, length = 16)
    private String userId;

    private Long permissionId;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(length = 16)
    private String phone;

    @Column(length = 15)
    private String ip;

    private String agent;

    private Timestamp accessTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStateEnum state = UserStateEnum.ACTIVE;

}
