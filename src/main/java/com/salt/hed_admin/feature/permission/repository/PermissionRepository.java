package com.salt.hed_admin.feature.permission.repository;

import com.salt.hed_admin.domain.permission.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionGroup, Long> {
}
