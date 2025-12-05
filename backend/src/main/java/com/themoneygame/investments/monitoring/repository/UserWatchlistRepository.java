package com.themoneygame.investments.monitoring.repository;

import com.themoneygame.investments.monitoring.domain.UserWatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWatchlistRepository extends JpaRepository<UserWatchlistEntry, Long> {

    List<UserWatchlistEntry> findByUserIdOrderByCreatedAtAsc(Long userId);

    Optional<UserWatchlistEntry> findByUserIdAndSymbol(Long userId, String symbol);

    void deleteByUserIdAndSymbol(Long userId, String symbol);
}
