package com.themoneygame.auth.infrastructure;

import com.themoneygame.auth.domain.Role;
import com.themoneygame.auth.domain.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);

    boolean existsByName(RoleType name);
}
