package com.salt.hed_admin.feature.token.repository;

import com.salt.hed_admin.domain.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByRefreshJtiAndRevokedFalse(String refreshJti);

    void deleteByRefreshJti(String refreshJti);
}
