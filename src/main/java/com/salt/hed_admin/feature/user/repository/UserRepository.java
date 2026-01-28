package com.salt.hed_admin.feature.user.repository;

import com.salt.hed_admin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
