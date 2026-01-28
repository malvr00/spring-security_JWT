package com.salt.hed_admin.domain.permission;

import com.salt.hed_admin.domain.BaseTimeEntity;
import com.salt.hed_admin.domain.permission.enums.PermissionTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "permission_group")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PermissionGroup extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    private PermissionTypeEnum permissionCategory;

    @Column(length = 15)
    private String name;

    private boolean isUse;

    private int orderNo;

}
