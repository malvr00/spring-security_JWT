package com.salt.hed_admin.domain.user;

import com.salt.hed_admin.domain.BaseTimeEntity;
import com.salt.hed_admin.domain.user.enums.UserStateEnum;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Entity
@Builder
@Table(name = "users")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private Long permissionId;

    @Column(nullable = false, length = 16)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(length = 16)
    private String phone;


    private Timestamp accessTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStateEnum state = UserStateEnum.ACTIVE;

}
