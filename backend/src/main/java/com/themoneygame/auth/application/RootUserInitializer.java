package com.themoneygame.auth.application;

import com.themoneygame.auth.domain.Role;
import com.themoneygame.auth.domain.RoleType;
import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.RoleRepository;
import com.themoneygame.auth.infrastructure.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RootUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RootUserInitializer(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // гарантируем, что роли существуют
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_USER)));

        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_ADMIN)));

        // создаём root-пользователя, если его ещё нет
        String rootUsername = "stasonix";

        if (!userRepository.existsByUsername(rootUsername)) {
            User root = new User(
                    rootUsername,
                    passwordEncoder.encode("Dev1ds0ne135$"),
                    "Root Admin"
            );
            root.addRole(userRole);
            root.addRole(adminRole);

            userRepository.save(root);
            System.out.println(">>> Root user 'stasonix' создан");
        } else {
            System.out.println(">>> Root user 'stasonix' уже существует");
        }
    }

}
